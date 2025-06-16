package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.MetaDataTableBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized.DataStatsMetaDataBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized.DimensionsMetaDataBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

import java.util.List;

/**
 * Facade service for metadata creation with various detail levels
 */
@Service
public class MetaDataCreationFacade {

    @Autowired
    private MetaDataTableBuilder metaDataTableBuilder;

    @Autowired
    private DimensionsMetaDataBuilder dimensionsBuilder;

    @Autowired
    private DataStatsMetaDataBuilder dataStatsBuilder;

    /**
     * Creates a basic metadata table with essential information
     */
    public MetaDataTable createCompleteMetaData(Indicateur indicateur) {
        return metaDataTableBuilder.buildCompleteMetaDataTable(indicateur);
    }

    /**
     * Creates a detailed metadata table with comprehensive information
     */
    public MetaDataTable createDetailedMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();

        // Add basic sections from the standard builder
        MetaDataTable basicTable = metaDataTableBuilder.buildCompleteMetaDataTable(indicateur);
        basicTable.getSections().forEach(table::addSection);

        // Add detailed specialized sections
        table.addSection(dimensionsBuilder.buildDetailedDimensionsSection(indicateur));
        table.addSection(dataStatsBuilder.buildDetailedDataStatsSection(indicateur));

        return table;
    }

    /**
     * Creates a metadata table focused on dimensions
     */
    public MetaDataTable createDimensionsFocusedMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();

        // Add basic info
        table.addSection(metaDataTableBuilder.buildCompleteMetaDataTable(indicateur).getSections().get(0));

        // Focus on dimensions
        table.addSection(dimensionsBuilder.buildDetailedDimensionsSection(indicateur));

        return table;
    }

    /**
     * Creates a metadata table focused on data statistics
     */
    public MetaDataTable createDataStatsFocusedMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();

        // Add basic info
        table.addSection(metaDataTableBuilder.buildCompleteMetaDataTable(indicateur).getSections().get(0));

        // Focus on data statistics
        table.addSection(dataStatsBuilder.buildDetailedDataStatsSection(indicateur));

        return table;
    }

    /**
     * Creates a selective metadata table with custom fields in the Information
     * section
     * 
     * @param indicateur      The indicator to create metadata for
     * @param columnsToExport List of specific fields to include in Information
     *                        section (e.g., "ID", "NOM", "DESCRIPTION")
     * @return MetaDataTable with selective Information section plus all other
     *         standard sections
     */
    public MetaDataTable createSelectiveMetaData(Indicateur indicateur, List<String> columnsToExport) {
        MetaDataTable table = new MetaDataTable();

        // If no specific columns requested, return complete metadata
        if (columnsToExport == null || columnsToExport.isEmpty()) {
            return createCompleteMetaData(indicateur);
        }

        // Build selective Information section with only requested fields
        table.addSection(metaDataTableBuilder.buildSelectiveInformationSection(indicateur, columnsToExport));

        return table;
    }

    /**
     * Creates a metadata table with only configuration information
     */
    public MetaDataTable createConfigurationMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(metaDataTableBuilder.buildConfigurationSection(indicateur));
        return table;
    }

    /**
     * Creates a metadata table with only data stats information
     */
    public MetaDataTable createDataStatsMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(metaDataTableBuilder.buildDataStatsSection(indicateur));
        return table;
    }
}
