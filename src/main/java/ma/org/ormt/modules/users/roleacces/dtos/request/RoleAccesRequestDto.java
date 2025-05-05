package ma.org.ormt.modules.users.roleacces.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "RoleAcces")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "roleAcces.id" }, allowGetters = true)
public class RoleAccesRequestDto extends Dto {

    @NotEmpty(message = "Ce champ est requis.")
    private String roleCode;

    @NotEmpty(message = "Ce champ est requis.")
    private String typeRessource;

    @NotEmpty(message = "Ce champ est requis.")
    private Long ressourceId;

    @NotBlank(message = "Ce champ est requis.")
    private String niveauAcces;

    private String description;
}