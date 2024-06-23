package ma.org.ancfcc.pva.modules.avion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Avion")
@JsonIgnoreProperties(value = { "avion.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class AvionDto extends BaseDto {

    private String matricule;

    private String marque;

    private String modele;
}