package ma.org.ormt.modules.chiffres.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;

@Setter
@Getter
@Schema(name = "ChiffreCleToDomaineDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "chiffrecledomaine.id" }, allowGetters = true)
public class ChiffreCleToDomaineDto extends Dto {

    private DomaineDto domaine;

}