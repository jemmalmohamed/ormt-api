package ma.org.ormt.modules.domaines.sousdomaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

@Mapper
public interface SousDomaineRequestDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineRequestDto> {

}
