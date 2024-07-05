package ma.org.ormt.modules.province.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.province.dto.ProvinceDto;

@Setter
@Getter
@Schema(name = "ProvinceDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "province.id" }, allowGetters = true)
public class ProvinceDetailDto extends ProvinceDto {

}
