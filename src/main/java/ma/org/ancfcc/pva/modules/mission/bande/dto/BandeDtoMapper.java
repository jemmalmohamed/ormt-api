package ma.org.ancfcc.pva.modules.mission.bande.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

@Mapper
public interface BandeDtoMapper extends BaseDtoMapper<Bande, BandeDto> {

}
