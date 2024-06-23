package ma.org.ancfcc.pva.modules.capteur.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Capteur")
@JsonIgnoreProperties(value = { "capteur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class CapteurDto extends BaseDto {

    private String nom;

    private String categorie;

    private String serial;

    private String mode;

    private String format;

    private String Description;

    private String constructeur;

}