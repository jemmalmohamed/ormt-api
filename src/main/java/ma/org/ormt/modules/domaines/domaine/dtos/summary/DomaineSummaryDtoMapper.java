package ma.org.ormt.modules.domaines.domaine.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

@Mapper
public interface DomaineSummaryDtoMapper extends BaseDtoMapper<Domaine, DomaineSummaryDto> {

}
