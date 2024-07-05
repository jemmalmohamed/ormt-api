package ma.org.ormt.modules.objet.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.objet.Objet;

@Mapper
public interface ObjetRequestMapper extends BaseDtoMapper<Objet, ObjetRequestDto> {

}
