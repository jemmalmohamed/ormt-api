package ma.org.ormt.modules.sousdomaine.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.sousdomaine.SousDomaine;

@Mapper
public interface SousDomaineDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineDto> {

}
