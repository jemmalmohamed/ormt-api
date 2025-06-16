package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a complete metadata table with multiple sections
 */
@Data
public class MetaDataTable {
    private List<MetaDataSection> sections;

    public MetaDataTable() {
        this.sections = new ArrayList<>();
    }

    public void addSection(MetaDataSection section) {
        if (section != null && !section.isEmpty()) {
            this.sections.add(section);
        }
    }

    public List<MetaDataRow> getAllRows() {
        return sections.stream()
                .flatMap(section -> section.getRows().stream())
                .collect(Collectors.toList());
    }

    public List<List<String>> toStringTable() {
        List<List<String>> result = new ArrayList<>();

        for (MetaDataSection section : sections) {
            // Add section title as a separator row if it's not the first section
            if (!result.isEmpty() && section.getTitle() != null && !section.getTitle().trim().isEmpty()) {
                result.add(List.of("", "")); // Empty separator row
                result.add(List.of(section.getTitle(), "")); // Section title row
            }

            // Add all rows from this section
            for (MetaDataRow row : section.getRows()) {
                result.add(List.of(row.getLabel(), row.getValue()));
            }
        }

        return result;
    }

    public boolean isEmpty() {
        return sections.isEmpty() || sections.stream().allMatch(MetaDataSection::isEmpty);
    }

    public int getTotalRowCount() {
        return sections.stream().mapToInt(MetaDataSection::getRowCount).sum();
    }
}
