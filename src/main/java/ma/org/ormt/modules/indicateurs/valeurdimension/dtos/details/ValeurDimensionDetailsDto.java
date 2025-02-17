package ma.org.ormt.modules.indicateurs.valeurdimension.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.ValeurDimensionDto;

@Setter
@Getter
@Schema(name = "ValeurDimensionDetailsDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "valeurdimension.id" }, allowGetters = true)
public class ValeurDimensionDetailsDto extends ValeurDimensionDto {
}