package ma.org.ormt.modules.partenaires.partenaire.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;

@Mapper
public interface PartenaireDtoMapper extends BaseDtoMapper<Partenaire, PartenaireDto> {

}
