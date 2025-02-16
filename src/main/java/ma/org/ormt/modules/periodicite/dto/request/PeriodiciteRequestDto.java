package ma.org.ormt.modules.periodicite.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;

@Builder
@Setter
@Getter
@Schema(name = "PeriodiciteRequestDto")
@RequiredArgsConstructor
@AllArgsConstructor
@Unique.List({
        @Unique(message = "Le code ${validatedValue.code} existe déjà", fieldName = "code", fieldId = "id", tableName = "periodicite"),
})
@JsonIgnoreProperties(value = { "periodicite.id" }, allowGetters = true)
public class PeriodiciteRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String code;

    @NotBlank(message = "Ce champ est requis.")
    private String libelle;
}