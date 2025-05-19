package ma.org.ormt.modules.chiffres.association.domaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;

@Mapper
public interface ChiffreCleDomaineDtoMapper extends BaseDtoMapper<ChiffreCleDomaine, ChiffreCleDomaineDto> {
}