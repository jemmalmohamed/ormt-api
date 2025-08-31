package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;

@Setter
@Getter
@Schema(name = "TBDomaineDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "TBdomaine.id" }, allowGetters = true)
public class TBDomaineDetailDto extends TBDomaineDto {

    // List<SousDomainePublicDto> sousDomaines;
    // List<SousDomaineSummaryDto> sousDomaines;

}
