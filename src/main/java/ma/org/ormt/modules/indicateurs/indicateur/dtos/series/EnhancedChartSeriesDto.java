package ma.org.ormt.modules.indicateurs.indicateur.dtos.series;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced DTO for chart series that supports multi-dimensional data
 * representation
 * Designed to handle secondary dimensions for more complex visualizations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedChartSeriesDto {

    // Basic series information
    private String name; // Name of the series (value of principal dimension)
    private String category; // Category of the series (name of principal dimension)
    private List<Object> labels; // Temporal labels (x-axis values)

    // Simple values for single-dimension visualization
    private List<Number> values; // Y-values when no secondary dimension is involved

    // Secondary dimension support
    private String secondaryDimension; // Name of the secondary dimension (if any)
    private List<String> secondaryValues; // List of available values for secondary dimension

    // Multi-dimensional data organization
    // Map of: secondaryValue -> list of values matching temporal order
    private Map<String, List<Number>> valuesBySecondary;
}