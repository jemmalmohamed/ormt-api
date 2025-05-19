package ma.org.ormt.modules.chiffres.dtos.details;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;

@Setter
@Getter
@Schema(name = "chiffrecleDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "chiffrecle.id" }, allowGetters = true)
public class ChiffreCleDetailsDto extends ChiffreCleDto {

    private List<ChiffreCleToDomaineDto> chiffrecleDomaines;

}