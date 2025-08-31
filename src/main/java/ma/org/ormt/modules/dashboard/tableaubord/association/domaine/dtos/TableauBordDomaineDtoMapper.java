package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;

@Mapper
public interface TableauBordDomaineDtoMapper extends BaseDtoMapper<TableauBordDomaine, TableauBordDomaineDto> {
}