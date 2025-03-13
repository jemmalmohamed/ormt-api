package ma.org.ormt.modules.indicateurs.source.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDto;

@Setter
@Getter
@Schema(name = "sourceDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "source.id" }, allowGetters = true)
public class SourceDetailsDto extends SourceDto {

}