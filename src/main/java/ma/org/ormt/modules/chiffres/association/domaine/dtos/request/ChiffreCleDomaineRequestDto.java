package ma.org.ormt.modules.chiffres.association.domaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;

@Setter
@Getter
@Schema(name = "ChiffreCleDo")
@RequiredArgsConstructor

@JsonIgnoreProperties(value = { "chiffrecleDo.id" }, allowGetters = true)
public class ChiffreCleDomaineRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis")
    private ChiffreCleDto chiffreCle;

    @NotNull(message = "Ce champ est requis")
    private DomaineDto domaine;

}