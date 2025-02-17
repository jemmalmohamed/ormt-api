package ma.org.ormt.modules.indicateurs.dimension.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "dimensionSummaryDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "dimension.id" }, allowGetters = true)
public class DimensionSummaryDto extends Dto {

    private String nom;

    private String type;

    private String libelle;
}