package ma.org.ormt.modules.capteur.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "CapteurDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "capteur.id" }, allowGetters = true)
public class CapteurSummaryDto extends Dto {

    private String nom;

    private String code;

    private String categorie;

    private String mode;

    private String format;

}
