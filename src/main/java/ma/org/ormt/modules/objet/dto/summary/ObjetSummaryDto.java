package ma.org.ormt.modules.objet.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "ObjetSummaryDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "objet.id" }, allowGetters = true)
public class ObjetSummaryDto extends Dto {

    private String nom;

}
