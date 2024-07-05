package ma.org.ormt.modules.capteur.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.capteur.Capteur;

@Mapper
public interface CapteurDetailDtoMapper extends BaseDtoMapper<Capteur, CapteurDetailDto> {

}
