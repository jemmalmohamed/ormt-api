package ma.org.ormt.modules.espaces.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "Espace")
@JsonIgnoreProperties(value = { "espace.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class EspaceDto extends Dto {

    private String nom;

    private String imageUrl;

    private String apropos;

    private String description;

    private String role;

    private String statut;

}
