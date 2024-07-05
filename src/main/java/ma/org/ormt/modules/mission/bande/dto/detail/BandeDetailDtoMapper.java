package ma.org.ormt.modules.mission.bande.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.bande.Bande;

@Mapper()
public interface BandeDetailDtoMapper extends BaseDtoMapper<Bande, BandeDetailDto> {

}
