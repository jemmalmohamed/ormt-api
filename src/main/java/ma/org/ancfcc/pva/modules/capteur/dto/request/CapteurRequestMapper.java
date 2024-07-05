package ma.org.ancfcc.pva.modules.capteur.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.capteur.Capteur;

@Mapper
public interface CapteurRequestMapper extends BaseDtoMapper<Capteur, CapteurRequestDto> {

}
