package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;

@Setter
@Getter
@Schema(name = "GrapheConfigurationRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "grapheConfiguration.id" }, allowGetters = true)
public class GrapheConfigurationRequestDto extends Dto {

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    @Schema(description = "ID de l'indicateur", required = true)
    private Long indicateurId;

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    @Schema(description = "ID du type de graphique", required = true)
    private Long grapheTypeId;

    @NotBlank(groups = { OnCreate.class, OnUpdate.class })
    @Schema(description = "Nom de la configuration", required = true)
    private String nom;

    @Schema(description = "Configuration Chart.js complète en JSON")
    private String configuration;

    @Schema(description = "Indique si c'est la configuration par défaut")
    private Boolean isDefault = false;

    @Schema(description = "Indique si la configuration est publique")
    private Boolean isPublic = false;

}