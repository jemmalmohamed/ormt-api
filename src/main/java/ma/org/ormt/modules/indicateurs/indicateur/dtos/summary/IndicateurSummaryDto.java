package ma.org.ormt.modules.indicateurs.indicateur.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "IndicateurSummaryDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurSummaryDto extends Dto {
}