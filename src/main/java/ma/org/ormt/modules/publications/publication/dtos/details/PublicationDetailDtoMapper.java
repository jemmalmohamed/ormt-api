package ma.org.ormt.modules.publications.publication.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.publications.publication.models.Publication;

@Mapper
public interface PublicationDetailDtoMapper extends BaseDtoMapper<Publication, PublicationDetailDto> {

}
