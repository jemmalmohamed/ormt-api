package ma.org.ormt.modules.indicateurs.donnee.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;

@Setter
@Getter
@Schema(name = "IndicateurDetailsDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "donneIndicateur.id" }, allowGetters = true)
public class DonneeIndicateurDetailsDto extends DonneeIndicateurDto {
}