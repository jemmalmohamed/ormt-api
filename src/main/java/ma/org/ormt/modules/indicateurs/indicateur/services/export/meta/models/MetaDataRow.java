package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models;

import lombok.Data;

/**
 * Represents a single row in a metadata table
 */
@Data
public class MetaDataRow {
    private String label;
    private String value;

    public MetaDataRow() {
        this.label = "";
        this.value = "";
    }

    public MetaDataRow(String label, String value) {
        this.label = label != null ? label : "";
        this.value = value != null ? value : "";
    }
}
