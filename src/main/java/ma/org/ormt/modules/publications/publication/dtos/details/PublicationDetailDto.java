package ma.org.ormt.modules.publications.publication.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDto;

@Setter
@Getter
@Schema(name = "PublicationDetail")
@JsonIgnoreProperties(value = { "publication.id" }, allowGetters = true)
public class PublicationDetailDto extends PublicationDto {

}
