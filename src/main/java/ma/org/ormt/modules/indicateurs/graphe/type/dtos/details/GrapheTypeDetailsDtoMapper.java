package ma.org.ormt.modules.indicateurs.graphe.type.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;

@Mapper
public interface GrapheTypeDetailsDtoMapper extends BaseDtoMapper<GrapheType, GrapheTypeDetailsDto> {
}