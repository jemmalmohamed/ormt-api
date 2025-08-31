package ma.org.ormt.modules.dashboard.tableaubord.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;

@Setter
@Getter
@Schema(name = "tableauBordDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "tableauBord.id" }, allowGetters = true)
public class TableauBordDetailsDto extends TableauBordDto {

    // private List<TbToDomaineDto> espaceDomaines;

}