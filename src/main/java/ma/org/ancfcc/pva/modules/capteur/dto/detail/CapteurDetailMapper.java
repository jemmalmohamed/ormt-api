package ma.org.ancfcc.pva.modules.capteur.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.capteur.Capteur;

@Mapper
public interface CapteurDetailMapper extends BaseDtoMapper<Capteur, CapteurDetailDto> {

}
