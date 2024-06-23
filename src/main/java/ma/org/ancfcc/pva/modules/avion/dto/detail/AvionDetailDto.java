package ma.org.ancfcc.pva.modules.avion.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.avion.dto.AvionDto;

@Setter
@Getter
@Schema(name = "AvionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "avion.id" }, allowGetters = true)
public class AvionDetailDto extends AvionDto {

}
