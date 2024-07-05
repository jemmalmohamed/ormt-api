package ma.org.ancfcc.pva.modules.mission.bande.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

@Mapper
public interface BandeRequestDtoMapper extends BaseDtoMapper<Bande, BandeRequestDto> {

}
