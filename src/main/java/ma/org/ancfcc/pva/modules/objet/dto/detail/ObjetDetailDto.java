package ma.org.ancfcc.pva.modules.objet.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.objet.dto.ObjetDto;

@Setter
@Getter
@Schema(name = "ObjetDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "objet.id" }, allowGetters = true)
public class ObjetDetailDto extends ObjetDto {

}
