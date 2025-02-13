package ma.org.ormt.modules.gis.province.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.gis.province.dto.ProvinceDto;

@Setter
@Getter
@Schema(name = "RegionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "region.id" }, allowGetters = true)
public class ProvinceDetailDto extends ProvinceDto {

}
