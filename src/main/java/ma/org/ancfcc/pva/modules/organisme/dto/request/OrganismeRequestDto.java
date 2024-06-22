package ma.org.ancfcc.pva.modules.organisme.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

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
@Schema(name = "Organisme")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "organisme"),
})
@JsonIgnoreProperties(value = { "organisme.id" }, allowGetters = true)
public class OrganismeRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotBlank(message = "Ce champ est requis.")
    private String secteur;

}