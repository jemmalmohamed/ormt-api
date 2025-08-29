package ma.org.ormt.modules.espaces.association.domaine.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;

@Setter
@Getter
@Schema(name = "EspaceToDomaineDto")
@RequiredArgsConstructor
public class EspaceDomaineDto extends Dto {

    private DomaineDto domaine;

    private EspaceDto espace;

    private Integer ordre;
}