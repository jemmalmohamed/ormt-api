package ma.org.ormt.modules.mission.bande.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.bande.Bande;

@Mapper
public interface BandeRequestDtoMapper extends BaseDtoMapper<Bande, BandeRequestDto> {

}
