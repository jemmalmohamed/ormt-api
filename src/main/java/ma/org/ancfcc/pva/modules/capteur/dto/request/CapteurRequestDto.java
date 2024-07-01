package ma.org.ancfcc.pva.modules.capteur.dto.request;

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
@Schema(name = "Capteur")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "capteur"),
})
@JsonIgnoreProperties(value = { "capteur.id" }, allowGetters = true)
public class CapteurRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotBlank(message = "Ce champ est requis.")
    private String code;

    @NotBlank(message = "Ce champ est requis.")
    private String categorie;

    private String serial;

    private String constructeur;

    private String description;

    @NotBlank(message = "Ce champ est requis.")
    private String mode;

    @NotBlank(message = "Ce champ est requis.")
    private String format;

}