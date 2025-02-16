package ma.org.ormt.modules.indicateur.dto.donnee;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateur.dto.valeurdimension.DonneeValeurDimensionDto;
import ma.org.ormt.modules.periodicite.dto.summary.PeriodiciteSummaryDto;

@Setter
@Getter
@Schema(name = "DonneeIndicateur")
@JsonIgnoreProperties(value = { "donneeIndicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DonneeIndicateurDto extends BaseDto {

    private String valeur;

    private List<DonneeValeurDimensionDto> valeursDimensions;

    private PeriodiciteSummaryDto periodicite;

    private Dto indicateur;
}
