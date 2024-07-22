package ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "DiplomeMilieu")
@JsonIgnoreProperties(value = { "diplomemilieu.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DiplomeMilieuSummaryDto extends Dto {

    private String nom;

    private String secteur;
}