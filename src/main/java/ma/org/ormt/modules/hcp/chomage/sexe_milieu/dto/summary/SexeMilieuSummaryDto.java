package ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "SexeMilieu")
@JsonIgnoreProperties(value = { "sexemilieu.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SexeMilieuSummaryDto extends Dto {

    private String nom;

    private String secteur;
}