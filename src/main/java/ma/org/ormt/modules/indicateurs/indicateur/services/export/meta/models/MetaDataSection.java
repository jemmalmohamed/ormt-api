package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section in metadata table with a title and rows
 */
@Data
public class MetaDataSection {
    private String title;
    private List<MetaDataRow> rows;

    public MetaDataSection() {
        this.title = "";
        this.rows = new ArrayList<>();
    }

    public MetaDataSection(String title) {
        this.title = title != null ? title : "";
        this.rows = new ArrayList<>();
    }

    public void addRow(MetaDataRow row) {
        if (row != null) {
            this.rows.add(row);
        }
    }

    public void addRow(String label, String value) {
        this.addRow(new MetaDataRow(label, value));
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public int getRowCount() {
        return rows.size();
    }
}
