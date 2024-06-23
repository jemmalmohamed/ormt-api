package ma.org.ancfcc.pva.modules.capteur.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.capteur.dto.CapteurDto;

@Setter
@Getter
@Schema(name = "CapteurDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "capteur.id" }, allowGetters = true)
public class CapteurDetailDto extends CapteurDto {

}
