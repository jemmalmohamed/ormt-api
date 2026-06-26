package ma.org.ormt.modules.dashboard.tbgroup.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "TbToDomaineDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "tbGroup.id" }, allowGetters = true)
public class TbToDomaineDto extends Dto {

    // private DomaineDto domaine;

    // private Integer ordre;

}