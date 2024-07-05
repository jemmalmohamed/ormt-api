package ma.org.ancfcc.pva.modules.objet.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.objet.Objet;

@Mapper
public interface ObjetSummaryDtoMapper extends BaseDtoMapper<Objet, ObjetSummaryDto> {

}
