package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDto;

@Setter
@Getter
@Schema(name = "grapheconfigurationDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "grapheconfiguration.id" }, allowGetters = true)
public class GrapheConfigurationDetailsDto extends GrapheConfigurationDto {

}