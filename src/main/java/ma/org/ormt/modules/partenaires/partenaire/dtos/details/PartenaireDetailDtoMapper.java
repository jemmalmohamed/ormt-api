package ma.org.ormt.modules.partenaires.partenaire.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;

@Mapper
public interface PartenaireDetailDtoMapper extends BaseDtoMapper<Partenaire, PartenaireDetailDto> {

}
