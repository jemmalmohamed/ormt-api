package ma.org.ormt.modules.indicateur.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateur.dto.IndicateurDto;

@Setter
@Getter
@Schema(name = "IndicateurDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurDetailDto extends IndicateurDto {
}