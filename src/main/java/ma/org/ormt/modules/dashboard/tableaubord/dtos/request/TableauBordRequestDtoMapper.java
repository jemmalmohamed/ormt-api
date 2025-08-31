package ma.org.ormt.modules.dashboard.tableaubord.dtos.request;

import org.mapstruct.Mapper;
import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;

@Mapper()
public interface TableauBordRequestDtoMapper extends BaseDtoMapper<TableauBord, TableauBordRequestDto> {
}
