package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;

@Setter
@Getter
@Schema(name = "TableauBordToDomaineDto")
@RequiredArgsConstructor
public class TableauBordDomaineDto extends Dto {

    private TBDomaineDto tbDomaine;

    private TableauBordDto tableauBord;

    private Integer ordre;
}