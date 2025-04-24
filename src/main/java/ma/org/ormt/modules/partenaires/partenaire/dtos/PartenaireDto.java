package ma.org.ormt.modules.partenaires.partenaire.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Partenaire")
@JsonIgnoreProperties(value = { "partenaire.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class PartenaireDto extends BaseDto {

    private String nom;

    private String description;

    private String role;

    private String statut;

    private String apropos;

}