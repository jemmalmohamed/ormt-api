package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;

@Setter
@Getter
@Schema(name = "TableauBordDomaineRequest")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "tableauBordDomaineRequest.id" }, allowGetters = true)
public class TableauBordDomaineRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis")
    private TableauBordDto tableauBord;

    @NotNull(message = "Ce champ est requis")
    private TBDomaineDto tbDomaine;

    @NotNull(message = "Ce champ est requis")
    private Integer ordre;

}