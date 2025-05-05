package ma.org.ormt.modules.domaines.sousdomaine.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@Setter
@Getter
@Schema(name = "SousDomaineDto")
@JsonIgnoreProperties(value = { "sousDomaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SousDomaineDto extends BaseDto {

    private String nom;

    private String description;

    private Boolean actif;

    private List<IndicateurDto> indicateurs;

    private Dto domaine;
}