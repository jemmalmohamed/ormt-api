package ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "SexeMilieu")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "sexeMilieu.id" }, allowGetters = true)
public class SexeMilieuRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String annee;

    @NotBlank(message = "Ce champ est requis.")
    private String sexe;

    @NotBlank(message = "Ce champ est requis.")
    private String milieu;

    @NotBlank(message = "Ce champ est requis.")
    private Float taux;

}