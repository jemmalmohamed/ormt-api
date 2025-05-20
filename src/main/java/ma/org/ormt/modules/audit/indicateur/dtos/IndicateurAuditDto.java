package ma.org.ormt.modules.audit.indicateur.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "IndicateurAuditDto")
@RequiredArgsConstructor
public class IndicateurAuditDto extends Dto {

}