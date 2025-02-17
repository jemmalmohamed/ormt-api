package ma.org.ormt.modules.domaines.sousdomaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@AllArgsConstructor
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le titre ${validatedValue.titre} existe déjà", fieldName = "titre", fieldId = "id", tableName = "sous_domaine"),
})
@JsonIgnoreProperties(value = { "sousdomaine.id" }, allowGetters = true)
public class SousDomaineRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String titre;

    @NotBlank(message = "Ce champ est requis.")
    private String description;

    @NotNull(message = "Le domaine est requis.")
    private Long idDomaine;
}