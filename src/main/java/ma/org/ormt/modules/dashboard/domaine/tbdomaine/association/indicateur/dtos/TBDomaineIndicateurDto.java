package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDto;

@Setter
@Getter
@Schema(name = "TBDomaineToIndicateurDto")
@RequiredArgsConstructor
public class TBDomaineIndicateurDto extends Dto {

    private IndicateurSousDomaineDetailDto indicateur;

    private Integer ordre;

    private String categorie;
}