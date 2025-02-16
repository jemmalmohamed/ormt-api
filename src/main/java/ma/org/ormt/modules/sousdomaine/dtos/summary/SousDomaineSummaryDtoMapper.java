package ma.org.ormt.modules.sousdomaine.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.sousdomaine.models.SousDomaine;

@Mapper
public interface SousDomaineSummaryDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineSummaryDto> {

}
