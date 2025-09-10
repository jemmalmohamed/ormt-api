package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "TBDomaine")
@JsonIgnoreProperties(value = { "TBDomaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class TBDomaineSummaryDto extends Dto {

    private String nom;

    private String libelle;

    private String description;
}