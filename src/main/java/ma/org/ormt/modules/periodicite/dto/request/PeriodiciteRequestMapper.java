package ma.org.ormt.modules.periodicite.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.periodicite.Periodicite;

@Mapper
public interface PeriodiciteRequestMapper extends BaseDtoMapper<Periodicite, PeriodiciteRequestDto> {
}