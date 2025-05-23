package ma.org.ormt.modules.indicateurs.indicateur.dtos.export;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor

public class IndicateurExportRequest {

    @NotBlank(message = "Ce champ est requis.")
    private String format;

    private boolean metaDataSheet;
    private boolean flatTableSheet;
    private boolean pivotTableSheet;
}