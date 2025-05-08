package ma.org.ormt.modules.indicateurs.indicateur.dtos.series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Mapper(componentModel = "spring")
public interface IndicateurChartDtoMapper extends BaseDtoMapper<Indicateur, IndicateurChartDto> {

    @AfterMapping
    default void afterMapping(Indicateur source, @MappingTarget IndicateurChartDto target,
            @Context Object... services) {
        if (source.getIndicateurDimensions() == null || source.getDonnees() == null) {
            return;
        }

        // Find temporal dimension
        IndicateurDimension temporalDim = source.getIndicateurDimensions().stream()
                .filter(id -> Boolean.TRUE.equals(id.getTemporelle()))
                .findFirst()
                .orElse(null);

        // Find non-temporal dimensions - these are all potential series dimensions
        List<IndicateurDimension> nonTemporalDims = source.getIndicateurDimensions().stream()
                .filter(id -> !Boolean.TRUE.equals(id.getTemporelle()))
                .collect(Collectors.toList());

        // Find principal dimension among non-temporal dimensions
        IndicateurDimension principalDim = nonTemporalDims.stream()
                .filter(id -> Boolean.TRUE.equals(id.getPrincipale()))
                .findFirst()
                .orElse(nonTemporalDims.isEmpty() ? null : nonTemporalDims.get(0));

        // Find secondary dimension (non-temporal and non-principal)
        IndicateurDimension secondaryDim = nonTemporalDims.stream()
                .filter(id -> !Boolean.TRUE.equals(id.getPrincipale()))
                .findFirst()
                .orElse(null);

        if (temporalDim == null || principalDim == null) {
            return;
        }

        // Set dimension metadata
        target.setTemporalDimension(temporalDim.getDimension().getNom());
        target.setPrincipalDimension(principalDim.getDimension().getNom());
        target.setAvailableDimensions(nonTemporalDims.stream()
                .map(id -> id.getDimension().getNom())
                .collect(Collectors.toList()));

        // Get sorted temporal values for consistent x-axis
        List<String> temporalValues = extractSortedTemporalValues(source, temporalDim);

        // Generate enhanced series with multi-dimension support
        List<EnhancedChartSeriesDto> enhancedSeries = createEnhancedSeries(
                source,
                temporalDim,
                principalDim,
                secondaryDim,
                temporalValues);

        target.setEnhancedSeries(enhancedSeries);

        // For backward compatibility, also set the regular series
        target.setSeries(convertToLegacySeries(enhancedSeries));

        // Create filter options for all dimensions
        createFilterOptions(target, source);
    }

    default List<EnhancedChartSeriesDto> createEnhancedSeries(
            Indicateur source,
            IndicateurDimension temporalDim,
            IndicateurDimension principalDim,
            IndicateurDimension secondaryDim,
            List<String> sortedTemporalValues) {

        List<EnhancedChartSeriesDto> result = new ArrayList<>();

        // Get all unique values for principal dimension
        List<String> principalValues = source.getDonnees().stream()
                .flatMap(d -> d.getValeurDimensions().stream())
                .filter(vd -> vd.getDimension().getId().equals(principalDim.getDimension().getId()))
                .map(ValeurDimension::getValeur)
                .distinct()
                .collect(Collectors.toList());

        // Get all unique values for secondary dimension (if it exists)
        List<String> secondaryValues = new ArrayList<>();
        if (secondaryDim != null) {
            secondaryValues = source.getDonnees().stream()
                    .flatMap(d -> d.getValeurDimensions().stream())
                    .filter(vd -> vd.getDimension().getId().equals(secondaryDim.getDimension().getId()))
                    .map(ValeurDimension::getValeur)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // For each principal value, create an enhanced series
        for (String principalValue : principalValues) {
            Map<String, Map<String, Number>> valuesBySecondaryAndTemporal = new HashMap<>();

            // If there's a secondary dimension, organize data by secondary dimension
            if (secondaryDim != null && !secondaryValues.isEmpty()) {
                for (String secondaryValue : secondaryValues) {
                    Map<String, Number> temporalMap = new HashMap<>();

                    // For each temporal value, find the data point
                    for (String temporalValue : sortedTemporalValues) {
                        Number value = findDataValue(
                                source,
                                temporalDim.getDimension(), temporalValue,
                                principalDim.getDimension(), principalValue,
                                secondaryDim.getDimension(), secondaryValue);

                        if (value != null) {
                            temporalMap.put(temporalValue, value);
                        }
                    }

                    valuesBySecondaryAndTemporal.put(secondaryValue, temporalMap);
                }
            } else {
                // No secondary dimension, just organize by temporal
                Map<String, Number> temporalMap = new HashMap<>();
                for (String temporalValue : sortedTemporalValues) {
                    Number value = findDataValue(
                            source,
                            temporalDim.getDimension(), temporalValue,
                            principalDim.getDimension(), principalValue,
                            null, null);

                    if (value != null) {
                        temporalMap.put(temporalValue, value);
                    }
                }

                // Use "default" as key when no secondary dimension exists
                valuesBySecondaryAndTemporal.put("default", temporalMap);
            }

            // Create the enhanced chart series
            EnhancedChartSeriesDto series = new EnhancedChartSeriesDto();
            series.setName(principalValue);
            series.setCategory(principalDim.getDimension().getNom());
            series.setLabels(new ArrayList<>(sortedTemporalValues));

            // If there's a secondary dimension, set secondary dimension info
            if (secondaryDim != null && !secondaryValues.isEmpty()) {
                series.setSecondaryDimension(secondaryDim.getDimension().getNom());
                series.setSecondaryValues(secondaryValues);

                // For each secondary value, construct a values array matching temporal order
                Map<String, List<Number>> valuesBySecondary = new HashMap<>();
                for (String secondaryValue : secondaryValues) {
                    List<Number> values = new ArrayList<>();
                    Map<String, Number> temporalMap = valuesBySecondaryAndTemporal.get(secondaryValue);

                    // Ensure values are in same order as temporal labels
                    for (String temporalValue : sortedTemporalValues) {
                        values.add(temporalMap != null ? temporalMap.get(temporalValue) : null);
                    }

                    valuesBySecondary.put(secondaryValue, values);
                }

                series.setValuesBySecondary(valuesBySecondary);
            } else {
                // No secondary dimension, just set regular values
                List<Number> values = new ArrayList<>();
                Map<String, Number> temporalMap = valuesBySecondaryAndTemporal.get("default");

                // Ensure values are in same order as temporal labels
                for (String temporalValue : sortedTemporalValues) {
                    values.add(temporalMap != null ? temporalMap.get(temporalValue) : null);
                }

                series.setValues(values);
            }

            result.add(series);
        }

        return result;
    }

    default Number findDataValue(
            Indicateur source,
            Dimension temporalDim, String temporalValue,
            Dimension principalDim, String principalValue,
            Dimension secondaryDim, String secondaryValue) {

        // Find the data point that matches all dimension values
        for (DonneeIndicateur donnee : source.getDonnees()) {
            boolean temporalMatch = false;
            boolean principalMatch = false;
            boolean secondaryMatch = secondaryDim == null || secondaryValue == null; // If no secondary dim, always
                                                                                     // match

            // Check all value dimensions for matches
            for (ValeurDimension vd : donnee.getValeurDimensions()) {
                if (vd.getDimension().getId().equals(temporalDim.getId()) &&
                        vd.getValeur().equals(temporalValue)) {
                    temporalMatch = true;
                } else if (vd.getDimension().getId().equals(principalDim.getId()) &&
                        vd.getValeur().equals(principalValue)) {
                    principalMatch = true;
                } else if (secondaryDim != null &&
                        vd.getDimension().getId().equals(secondaryDim.getId()) &&
                        vd.getValeur().equals(secondaryValue)) {
                    secondaryMatch = true;
                }
            }

            // If all dimensions match, return the value
            if (temporalMatch && principalMatch && secondaryMatch) {
                return parseDataValue(donnee.getValeur());
            }
        }

        return null;
    }

    default List<ChartSeriesDto> convertToLegacySeries(List<EnhancedChartSeriesDto> enhancedSeries) {
        // Convert enhanced series to legacy format for backward compatibility
        List<ChartSeriesDto> legacySeries = new ArrayList<>();

        for (EnhancedChartSeriesDto enhanced : enhancedSeries) {
            ChartSeriesDto legacy = new ChartSeriesDto();
            legacy.setName(enhanced.getName());
            legacy.setCategory(enhanced.getCategory());
            legacy.setLabels(enhanced.getLabels());

            // If there are secondary values, flatten them
            if (enhanced.getValuesBySecondary() != null && !enhanced.getValuesBySecondary().isEmpty()) {
                // Just use the first secondary value for the legacy format
                String firstSecondaryKey = enhanced.getValuesBySecondary().keySet().iterator().next();
                legacy.setValues(enhanced.getValuesBySecondary().get(firstSecondaryKey));
            } else {
                legacy.setValues(enhanced.getValues());
            }

            legacySeries.add(legacy);
        }

        return legacySeries;
    }

    default List<String> extractSortedTemporalValues(Indicateur source, IndicateurDimension temporalDim) {
        // Extract all temporal values and sort them
        return source.getDonnees().stream()
                .map(donnee -> findDimensionValue(donnee, temporalDim.getDimension()))
                .filter(value -> value != null)
                .distinct()
                .sorted() // Natural sorting, might need custom comparator for special formats
                .collect(Collectors.toList());
    }

    default Number parseDataValue(String value) {
        if (value == null || value.isEmpty())
            return null;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default String findDimensionValue(DonneeIndicateur donnee, Dimension dimension) {
        if (donnee.getValeurDimensions() == null)
            return null;

        for (ValeurDimension valeurDimension : donnee.getValeurDimensions()) {
            if (valeurDimension.getDimension().getId().equals(dimension.getId())) {
                return valeurDimension.getValeur();
            }
        }
        return null;
    }

    default void createFilterOptions(IndicateurChartDto target, Indicateur source) {
        List<DimensionValueDto> filterOptions = new ArrayList<>();

        // For each dimension, collect available values
        for (IndicateurDimension indicateurDimension : source.getIndicateurDimensions()) {
            Dimension dimension = indicateurDimension.getDimension();

            // Skip if this is the temporal dimension that will be on the X axis
            if (Boolean.TRUE.equals(indicateurDimension.getTemporelle())) {
                continue; // Already handled as x-axis
            }

            // Collect unique values for this dimension
            List<String> values = source.getDonnees().stream()
                    .flatMap(donnee -> donnee.getValeurDimensions().stream())
                    .filter(vd -> vd.getDimension().getId().equals(dimension.getId()))
                    .map(ValeurDimension::getValeur)
                    .distinct()
                    .collect(Collectors.toList());

            // Create filter option
            DimensionValueDto filterOption = DimensionValueDto.builder()
                    .dimensionName(dimension.getNom())
                    .dimensionLibelle(dimension.getLibelle())
                    .isPrincipal(Boolean.TRUE.equals(indicateurDimension.getPrincipale()))
                    .isTemporal(Boolean.TRUE.equals(indicateurDimension.getTemporelle()))
                    .availableValues(values)
                    .selectedValues(new ArrayList<>()) // Default: none selected
                    .build();

            filterOptions.add(filterOption);
        }

        target.setFilterOptions(filterOptions);
    }
}