package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;

@Mapper
public interface GrapheConfigurationDetailsDtoMapper
                extends BaseDtoMapper<GrapheConfiguration, GrapheConfigurationDetailsDto> {
}