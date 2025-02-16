package ma.org.ormt.modules.periodicite.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Periodicite")
@JsonIgnoreProperties(value = { "periodicite.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class PeriodiciteDto extends BaseDto {

    private String code;
    private String libelle;
}