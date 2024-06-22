package ma.org.ancfcc.pva.modules.organisme.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.organisme.dto.OrganismeDto;

@Setter
@Getter
@Schema(name = "OrganismeDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "organisme.id" }, allowGetters = true)
public class OrganismeDetailDto extends OrganismeDto {

}
