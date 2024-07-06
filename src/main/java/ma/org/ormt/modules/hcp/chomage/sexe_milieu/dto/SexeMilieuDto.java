package ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "sexeMilieu")
@JsonIgnoreProperties(value = { "sexeMilieu.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class SexeMilieuDto extends BaseDto {

    private String annee;

    private String sexe;

    private String milieu;

    private Float taux;
}