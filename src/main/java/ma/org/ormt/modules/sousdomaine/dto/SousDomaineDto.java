package ma.org.ormt.modules.sousdomaine.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateur.dto.IndicateurDto;

@Setter
@Getter
@Schema(name = "SousDomaineDto")
@JsonIgnoreProperties(value = { "sousDomaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SousDomaineDto extends BaseDto {

    private String titre;

    private String description;

    private List<IndicateurDto> indicateurs;

    private Dto domaine;
}