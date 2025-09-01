package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@Setter
@Getter
@Schema(name = "TBDomaineDo")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "tbDomaineDo.id" }, allowGetters = true)
public class TBDomaineIndicateurRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis")
    private TBDomaineDto tbDomaine;

    @NotNull(message = "Ce champ est requis")
    private IndicateurDto indicateur;

    private String categorie;

    @NotNull(message = "Ce champ est requis")
    private Integer ordre;

}