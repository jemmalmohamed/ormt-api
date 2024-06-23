package ma.org.ancfcc.pva.modules.planaction.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import ma.org.ancfcc.pva.core.commun.base.dto.Dto;
import ma.org.ancfcc.pva.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "PlanAction")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "La nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "plan_action"),
})
@JsonIgnoreProperties(value = { "planAction.id" }, allowGetters = true)
public class PlanActionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.e")
    private String nom;

    @NotNull(message = "Ce champ est requis.")
    private LocalDateTime debutDate;

    @NotNull(message = "Ce champ est requis.")
    private LocalDateTime finDate;

    private String description;

}