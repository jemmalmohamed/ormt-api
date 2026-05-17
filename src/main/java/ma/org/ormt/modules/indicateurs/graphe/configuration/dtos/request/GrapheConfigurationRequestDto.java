package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "GrapheConfigurationRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "grapheConfiguration.id" }, allowGetters = true)
public class GrapheConfigurationRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis")
    private Dto indicateur;

    @NotNull(message = "Ce champ est requis")
    private Dto grapheType;

    @Schema(description = "Nom de la configuration")
    private String nom;

    private String chartOptionsJson;

    private Boolean isDefault = false;

}