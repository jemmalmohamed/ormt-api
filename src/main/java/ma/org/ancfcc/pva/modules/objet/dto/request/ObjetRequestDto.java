package ma.org.ancfcc.pva.modules.objet.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import ma.org.ancfcc.pva.core.commun.base.dto.Dto;
import ma.org.ancfcc.pva.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "Objet")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "objet"),
})
@JsonIgnoreProperties(value = { "objet.id" }, allowGetters = true)
public class ObjetRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    private String description;

}