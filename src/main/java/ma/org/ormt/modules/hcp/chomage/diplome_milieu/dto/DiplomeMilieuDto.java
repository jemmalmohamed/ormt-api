package ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "diplomeMilieu")
@JsonIgnoreProperties(value = { "diplomeMilieu.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DiplomeMilieuDto extends BaseDto {

    private String annee;

    private String diplome;

    private String milieu;

    private float taux;
}