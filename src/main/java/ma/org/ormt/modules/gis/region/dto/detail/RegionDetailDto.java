package ma.org.ormt.modules.gis.region.dto.detail;

import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.geometry.serializer.MultiPolygonSerializer;
import ma.org.ormt.modules.gis.region.dto.RegionDto;

@Setter
@Getter
@Schema(name = "RegionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "region.id" }, allowGetters = true)
public class RegionDetailDto extends RegionDto {

    @JsonSerialize(using = MultiPolygonSerializer.class)
    private MultiPolygon delimitation;

}
