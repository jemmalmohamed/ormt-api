package ma.org.ormt.modules.capteur.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.capteur.Capteur;

@Mapper
public interface CapteurRequestMapper extends BaseDtoMapper<Capteur, CapteurRequestDto> {

}
