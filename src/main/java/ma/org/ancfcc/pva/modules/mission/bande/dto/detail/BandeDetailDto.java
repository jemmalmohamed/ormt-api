package ma.org.ancfcc.pva.modules.mission.bande.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.mission.bande.dto.BandeDto;

@Setter
@Getter
@Schema(name = "Bande")
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BandeDetailDto extends BandeDto {

}