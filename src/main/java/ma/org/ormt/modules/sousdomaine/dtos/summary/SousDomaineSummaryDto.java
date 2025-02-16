package ma.org.ormt.modules.sousdomaine.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "SousDomaine")
@JsonIgnoreProperties(value = { "sousdomaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SousDomaineSummaryDto extends Dto {

    private String titre;

    private String description;

}