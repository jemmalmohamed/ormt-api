package ma.org.ormt.modules.sousdomaine.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.sousdomaine.SousDomaine;

@Mapper
public interface SousDomaineSummaryDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineSummaryDto> {

}
