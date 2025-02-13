package ma.org.ormt.modules.gis.basemap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Basemap")
@JsonIgnoreProperties(value = { "basemap.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class BasemapDto extends BaseDto {

    private String nom;

    private String url;

}