package ma.org.ormt.modules.indicateurs.indicateur.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.series.IndicateurChartDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.series.IndicateurChartDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicateurChartService {

    private final IndicateurRepository indicateurRepository;
    private final IndicateurChartDtoMapper indicateurChartDtoMapper;

    /**
     * Get chart data for an indicator
     * 
     * @param indicateurId The indicator ID
     * @return The chart data DTO
     */
    @Transactional(readOnly = true)
    public IndicateurChartDto getChartData(Long indicateurId) {
        log.debug("Request to get chart data for indicator : {}", indicateurId);

        // Indicateur indicateur =
        // indicateurRepository.findByIdWithDonneesAndDimensions(indicateurId)
        // .orElseThrow(() -> new RuntimeException("Indicateur not found with id: " +
        // indicateurId));
        Indicateur indicateur = indicateurRepository.findById(indicateurId)
                .orElseThrow(() -> new RuntimeException("Indicateur not found with id: " + indicateurId));

        return indicateurChartDtoMapper.mapToDto(indicateur);
    }

    /**
     * Get chart data for an indicator with filters
     * 
     * @param indicateurId The indicator ID
     * @param filters      Map of dimension name -> list of selected values
     * @return The filtered chart data DTO
     */
    @Transactional(readOnly = true)
    public IndicateurChartDto getFilteredChartData(Long indicateurId, Map<String, List<String>> filters) {
        log.debug("Request to get filtered chart data for indicator : {}", indicateurId);

        // Indicateur indicateur =
        // indicateurRepository.findByIdWithDonneesAndDimensions(indicateurId)
        // .orElseThrow(() -> new RuntimeException("Indicateur not found with id: " +
        // indicateurId));
        Indicateur indicateur = indicateurRepository.findById(indicateurId)
                .orElseThrow(() -> new RuntimeException("Indicateur not found with id: " + indicateurId));

        // Apply filters by removing data points that don't match filter criteria
        if (filters != null && !filters.isEmpty()) {
            filterIndicatorData(indicateur, filters);
        }

        return indicateurChartDtoMapper.mapToDto(indicateur);
    }

    /**
     * Filter indicator data based on dimension filters
     * 
     * @param indicateur The indicator with data
     * @param filters    Map of dimension name -> list of selected values
     */
    private void filterIndicatorData(Indicateur indicateur, Map<String, List<String>> filters) {
        List<DonneeIndicateur> donnees = indicateur.getDonnees();

        // If no data, nothing to filter
        if (donnees == null || donnees.isEmpty()) {
            return;
        }

        // Find dimensions by name
        Map<String, IndicateurDimension> dimensionMap = indicateur.getIndicateurDimensions().stream()
                .collect(java.util.stream.Collectors.toMap(
                        id -> id.getDimension().getNom(),
                        id -> id));

        // Filter data points
        List<DonneeIndicateur> filteredDonnees = donnees.stream()
                .filter(donnee -> matchesFilters(donnee, filters, dimensionMap))
                .collect(java.util.stream.Collectors.toList());

        indicateur.setDonnees(filteredDonnees);
    }

    /**
     * Check if a data point matches all filters
     * 
     * @param donnee       The data point
     * @param filters      Map of dimension name -> list of selected values
     * @param dimensionMap Map of dimension name -> indicateur dimension
     * @return true if matches all filters
     */
    private boolean matchesFilters(DonneeIndicateur donnee, Map<String, List<String>> filters,
            Map<String, IndicateurDimension> dimensionMap) {
        // For each filter dimension
        for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
            String dimensionName = filter.getKey();
            List<String> selectedValues = filter.getValue();

            // If no values selected for this dimension, skip filter
            if (selectedValues == null || selectedValues.isEmpty()) {
                continue;
            }

            // Get dimension
            IndicateurDimension dimension = dimensionMap.get(dimensionName);
            if (dimension == null) {
                continue; // Dimension not found for indicator
            }

            // Check if this data point has one of the selected values for this dimension
            boolean hasMatchingValue = donnee.getValeurDimensions().stream()
                    .filter(vd -> vd.getDimension().getId().equals(dimension.getDimension().getId()))
                    .anyMatch(vd -> selectedValues.contains(vd.getValeur()));

            if (!hasMatchingValue) {
                return false; // This data point doesn't match one of the filters
            }
        }

        return true; // Matches all filters
    }
}