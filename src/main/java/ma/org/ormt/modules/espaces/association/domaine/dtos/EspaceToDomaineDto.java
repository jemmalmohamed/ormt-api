package ma.org.ormt.modules.espaces.association.domaine.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;

@Setter
@Getter
@Schema(name = "EspaceToDomaineDto")
@RequiredArgsConstructor
public class EspaceToDomaineDto extends Dto {

    private List<DomaineDto> domaines;

}