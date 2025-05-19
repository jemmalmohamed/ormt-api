package ma.org.ormt.modules.chiffres.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

// Add 'uses' property to specify nested mappers
@Mapper()
public interface ChiffreCleDtoMapper extends BaseDtoMapper<ChiffreCle, ChiffreCleDto> {

}