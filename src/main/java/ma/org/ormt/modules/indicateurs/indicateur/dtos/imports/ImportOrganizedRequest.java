package ma.org.ormt.modules.indicateurs.indicateur.dtos.imports;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ma.org.ormt.modules.indicateurs.indicateur.helpers.ImportXlsResult.ImportedRow;

@Setter
@Getter
@Schema(name = "ImportOrganizedRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ImportOrganizedRequest {

    @Schema(description = "List of mapper rows")
    private List<MapperRow> mapperRowList;

    @Schema(description = "List of imported rows")
    private List<ImportedRow> rowDataList;

    @Setter
    @Getter
    @Schema(name = "MapperRow")
    @AllArgsConstructor
    @RequiredArgsConstructor
    @ToString
    public static class MapperRow {

        @Schema(description = "Index of the row")
        private Integer rowIndex;

        @Schema(description = "ID of the dimension")
        private Integer dimensionId;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            MapperRow mapperRow = (MapperRow) o;
            return Objects.equals(rowIndex, mapperRow.rowIndex) && Objects.equals(dimensionId, mapperRow.dimensionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rowIndex, dimensionId);
        }
    }
}