package ma.org.ormt.modules.indicateur.dto.donnee;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateur.models.DonneeIndicateur;

@Mapper
public interface DonneeIndicateurDtoMapper extends BaseDtoMapper<DonneeIndicateur, DonneeIndicateurDto> {

}