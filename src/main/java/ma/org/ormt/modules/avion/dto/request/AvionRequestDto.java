package ma.org.ormt.modules.avion.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "Avion")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le matricule ${validatedValue.matricule} existe déjà", fieldName = "matricule", fieldId = "id", tableName = "avion"),
})
@JsonIgnoreProperties(value = { "avion.id" }, allowGetters = true)
public class AvionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String matricule;

    private String marque;

    private String modele;

}