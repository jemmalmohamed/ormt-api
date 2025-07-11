package ma.org.ormt.modules.indicateurs.graphe.type.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;

@Mapper
public interface GrapheTypeDtoMapper extends BaseDtoMapper<GrapheType, GrapheTypeDto> {
}