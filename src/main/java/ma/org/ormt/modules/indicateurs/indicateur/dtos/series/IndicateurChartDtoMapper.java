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

        // Find principal and temporal dimensions
        IndicateurDimension principalDim = source.getIndicateurDimensions().stream()
                .filter(id -> Boolean.TRUE.equals(id.getPrincipale()))
                .findFirst()
                .orElse(null);

        IndicateurDimension temporalDim = source.getIndicateurDimensions().stream()
                .filter(id -> Boolean.TRUE.equals(id.getTemporelle()))
                .findFirst()
                .orElse(null);

        if (principalDim == null || temporalDim == null) {
            return;
        }

        // Set dimension metadata
        target.setPrincipalDimension(principalDim.getDimension().getNom());
        target.setTemporalDimension(temporalDim.getDimension().getNom());
        target.setAvailableDimensions(source.getIndicateurDimensions().stream()
                .map(id -> id.getDimension().getNom())
                .collect(Collectors.toList()));

        // Process data and create series
        processSeriesData(target, source, principalDim, temporalDim);

        // Create filter options
        createFilterOptions(target, source);
    }

    default void processSeriesData(IndicateurChartDto target, Indicateur source,
            IndicateurDimension principalDim, IndicateurDimension temporalDim) {
        // Map to store data by principal dimension value and temporal dimension value
        Map<String, Map<String, Number>> seriesDataMap = new HashMap<>();
        List<String> allTemporalValues = new ArrayList<>();

        // Process each data point
        for (DonneeIndicateur donnee : source.getDonnees()) {
            // Find principal dimension value for this data point
            String principalValue = findDimensionValue(donnee, principalDim.getDimension());
            if (principalValue == null)
                continue;

            // Find temporal dimension value for this data point
            String temporalValue = findDimensionValue(donnee, temporalDim.getDimension());
            if (temporalValue == null)
                continue;

            // Add to temporal values list (for later sorting)
            if (!allTemporalValues.contains(temporalValue)) {
                allTemporalValues.add(temporalValue);
            }

            // Parse the string value to a Number before adding to the map
            try {
                String valeurStr = donnee.getValeur();
                Number valeurNum;
                if (valeurStr != null && !valeurStr.isEmpty()) {
                    // Try to parse as double first (handles decimal values)
                    valeurNum = Double.parseDouble(valeurStr);
                } else {
                    valeurNum = null;
                }
                seriesDataMap.computeIfAbsent(principalValue, k -> new HashMap<>())
                        .put(temporalValue, valeurNum);
            } catch (NumberFormatException e) {
                // If parsing fails, we can either log a warning or use null
                seriesDataMap.computeIfAbsent(principalValue, k -> new HashMap<>())
                        .put(temporalValue, null);
            }
        }

        // Sort temporal values (assuming they can be naturally ordered)
        allTemporalValues.sort(String::compareTo); // For proper ordering, may need custom comparator

        // Create series for each principal dimension value
        List<ChartSeriesDto> series = new ArrayList<>();
        for (Map.Entry<String, Map<String, Number>> entry : seriesDataMap.entrySet()) {
            String principalValue = entry.getKey();
            Map<String, Number> temporalData = entry.getValue();

            List<Object> labels = new ArrayList<>(allTemporalValues);
            List<Number> values = new ArrayList<>();

            // Create values list in same order as labels
            for (String temporalValue : allTemporalValues) {
                values.add(temporalData.getOrDefault(temporalValue, null));
            }

            // Create series
            ChartSeriesDto chartSeries = ChartSeriesDto.builder()
                    .name(principalValue)
                    .category(principalDim.getDimension().getNom())
                    .labels(labels)
                    .values(values)
                    .build();

            series.add(chartSeries);
        }

        target.setSeries(series);
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