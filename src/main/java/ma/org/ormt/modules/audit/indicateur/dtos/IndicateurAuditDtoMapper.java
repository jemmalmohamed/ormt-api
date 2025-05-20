package ma.org.ormt.modules.audit.indicateur.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Mapper
public interface IndicateurAuditDtoMapper extends BaseDtoMapper<Indicateur, IndicateurAuditDto> {
}