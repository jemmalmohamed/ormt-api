package ma.org.ormt.modules.indicateurs.graphe.type.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.GrapheTypeDto;

@Setter
@Getter
@Schema(name = "graphetypeDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "graphetype.id" }, allowGetters = true)
public class GrapheTypeDetailsDto extends GrapheTypeDto {

}