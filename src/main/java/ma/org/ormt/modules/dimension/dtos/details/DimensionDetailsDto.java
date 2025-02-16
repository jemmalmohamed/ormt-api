package ma.org.ormt.modules.dimension.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.dimension.dtos.DimensionDto;

@Setter
@Getter
@Schema(name = "dimensionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "dimension.id" }, allowGetters = true)
public class DimensionDetailsDto extends DimensionDto {

}