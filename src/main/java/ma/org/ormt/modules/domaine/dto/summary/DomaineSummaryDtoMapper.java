package ma.org.ormt.modules.domaine.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.Domaine;

@Mapper
public interface DomaineSummaryDtoMapper extends BaseDtoMapper<Domaine, DomaineSummaryDto> {

}
