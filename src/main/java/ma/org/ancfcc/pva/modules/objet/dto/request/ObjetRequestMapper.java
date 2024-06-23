package ma.org.ancfcc.pva.modules.objet.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.objet.Objet;

@Mapper
public interface ObjetRequestMapper extends BaseDtoMapper<Objet, ObjetRequestDto> {

}
