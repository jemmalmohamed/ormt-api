package ma.org.ormt.modules.province.dto;

import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.geometry.serializer.MultiPolygonSerializer;

@Setter
@Getter
@Schema(name = "Province")
@JsonIgnoreProperties(value = { "province.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDto extends BaseDto {

    private String nom;

    private Long superficie;

    private String description;

    private String typeCollectivite;

    @JsonSerialize(using = MultiPolygonSerializer.class)
    private MultiPolygon delimitation;

}