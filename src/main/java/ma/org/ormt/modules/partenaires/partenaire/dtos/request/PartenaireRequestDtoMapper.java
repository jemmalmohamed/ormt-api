package ma.org.ormt.modules.partenaires.partenaire.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;

@Mapper
public interface PartenaireRequestDtoMapper extends BaseDtoMapper<Partenaire, PartenaireRequestDto> {

}
