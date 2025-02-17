package ma.org.ormt.modules.indicateurs.donnee.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

@Mapper
public interface DonneeIndicateurDtoMapper extends BaseDtoMapper<DonneeIndicateur, DonneeIndicateurDto> {

}