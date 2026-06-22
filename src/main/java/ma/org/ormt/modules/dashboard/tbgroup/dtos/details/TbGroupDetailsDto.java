package ma.org.ormt.modules.dashboard.tbgroup.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDto;

@Setter
@Getter
@Schema(name = "tbGroupDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "tbGroup.id" }, allowGetters = true)
public class TbGroupDetailsDto extends TbGroupDto {
}
