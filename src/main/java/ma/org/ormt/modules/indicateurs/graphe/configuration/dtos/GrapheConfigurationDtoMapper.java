package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;

@Mapper
public interface GrapheConfigurationDtoMapper extends BaseDtoMapper<GrapheConfiguration, GrapheConfigurationDto> {
}