package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.MetaDataTableBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized.DataStatsMetaDataBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized.DomainesMetaDataBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized.HorizontalDimensionsMetaDataBuilder;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

/**
 * Facade service for metadata creation with various detail levels
 */
@Service
public class MetaDataCreationFacade {

    @Autowired
    private MetaDataTableBuilder metaDataTableBuilder;

    @Autowired
    private DataStatsMetaDataBuilder dataStatsBuilder;

    @Autowired
    private DomainesMetaDataBuilder domainesBuilder;

    @Autowired
    private HorizontalDimensionsMetaDataBuilder horizontalDimensionsBuilder;

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
    public MetaDataTable createInformationMetaData(Indicateur indicateur, List<String> columnsToExport) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(metaDataTableBuilder.buildInformationSection(indicateur, columnsToExport));
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

    /**
     * Creates a metadata table with detailed data statistics
     */
    public MetaDataTable createDataStatsFocusedMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(dataStatsBuilder.buildDetailedDataStatsSection(indicateur));
        return table;
    }

    /**
     * Creates a metadata table with comprehensive domains information
     * This replaces the old direct Excel rendering approach
     */
    public MetaDataTable createDomainesMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(domainesBuilder.buildDomainesSection(indicateur));
        return table;
    }

    /**
     * Creates a metadata table with essential domains information only
     */
    public MetaDataTable createEssentialDomainesMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        table.addSection(domainesBuilder.buildEssentialDomainesSection(indicateur));
        return table;
    }

    /**
     * Creates a metadata table with horizontal dimensions layout (original format)
     * This preserves the original createDimensionsTable horizontal columns approach
     */
    public MetaDataTable createHorizontalDimensionsMetaData(Indicateur indicateur) {
        return horizontalDimensionsBuilder.buildHorizontalDimensionsTable(indicateur);
    }
}
