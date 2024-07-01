package ma.org.ancfcc.pva.modules.capteur.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.capteur.Capteur;

@Mapper
public interface CapteurSummaryDtoMapper extends BaseDtoMapper<Capteur, CapteurSummaryDto> {

}
