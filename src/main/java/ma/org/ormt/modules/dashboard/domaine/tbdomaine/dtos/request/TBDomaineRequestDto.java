package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "TBDomaine")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "tb_domaine"),
})
@JsonIgnoreProperties(value = { "tbDomaine.id" }, allowGetters = true)
public class TBDomaineRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    @NotBlank(message = "Ce champ est requis.")
    private String description;

}