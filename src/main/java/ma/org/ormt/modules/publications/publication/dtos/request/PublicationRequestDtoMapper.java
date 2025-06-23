package ma.org.ormt.modules.publications.publication.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.publications.publication.models.Publication;

@Mapper
public interface PublicationRequestDtoMapper extends BaseDtoMapper<Publication, PublicationRequestDto> {

}
