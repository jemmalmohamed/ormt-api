package ma.org.ormt.modules.espaces.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;

@Setter
@Getter
@Schema(name = "espaceDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "espace.id" }, allowGetters = true)
public class EspaceDetailsDto extends EspaceDto {

}