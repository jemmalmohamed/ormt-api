package ma.org.ormt.modules.chiffres.dtos.request;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

@Mapper
public interface ChiffreCleRequestDtoMapper extends BaseDtoMapper<ChiffreCle, ChiffreCleRequestDto> {

	@Override
	default List<ChiffreCleRequestDto> mapToDto(List<ChiffreCle> list, @Context Object... services) {
		if (list == null) {
			return null;
		}

		return list.stream()
				.map(entity -> mapToDto(entity, services))
				.toList();
	}
}