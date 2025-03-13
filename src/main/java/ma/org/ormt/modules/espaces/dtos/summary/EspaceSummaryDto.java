package ma.org.ormt.modules.espaces.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "espaceSummaryDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "espace.id" }, allowGetters = true)
public class EspaceSummaryDto extends Dto {

    private String nom;

    private String image;

}