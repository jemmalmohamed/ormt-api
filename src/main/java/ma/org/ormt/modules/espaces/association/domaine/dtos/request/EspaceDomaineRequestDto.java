package ma.org.ormt.modules.espaces.association.domaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;

@Setter
@Getter
@Schema(name = "EspaceDo")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "espaceDo.id" }, allowGetters = true)
public class EspaceDomaineRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis")
    private EspaceDto espace;

    @NotNull(message = "Ce champ est requis")
    private DomaineDto domaine;

}