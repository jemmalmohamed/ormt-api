package ma.org.ormt.modules.sousdomaine.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.sousdomaine.SousDomaine;

@Mapper
public interface SousDomaineRequestMapper extends BaseDtoMapper<SousDomaine, SousDomaineRequestDto> {

}
