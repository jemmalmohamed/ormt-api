package ma.org.ormt.modules.indicateurs.indicateur.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

import ma.org.ormt.modules.domaines.sousdomaine.dtos.summary.SousDomaineSummaryDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dtos.IndicateurDimensionDto;

@Setter
@Getter
@Schema(name = "Indicateur")
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurDto extends BaseDto {

    private String nom;

    private String source;

    private String regleCalcul;

    private String unite;

    private String typeTb;

    @JsonProperty("dimensions")
    private List<IndicateurDimensionDto> indicateurDimensions;

    private List<DonneeIndicateurDto> donnees;

    private List<SousDomaineSummaryDto> sousDomaines;
}