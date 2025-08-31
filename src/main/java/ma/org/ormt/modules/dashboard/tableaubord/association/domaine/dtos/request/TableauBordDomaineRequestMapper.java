package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;

@Mapper
public interface TableauBordDomaineRequestMapper
        extends BaseDtoMapper<TableauBordDomaine, TableauBordDomaineRequestDto> {

}
