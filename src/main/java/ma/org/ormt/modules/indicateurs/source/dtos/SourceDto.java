package ma.org.ormt.modules.indicateurs.source.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "Source")
@JsonIgnoreProperties(value = { "source.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SourceDto extends Dto {

    private String nom;

    private String type;

    private String description;

    private String libelle;

}
