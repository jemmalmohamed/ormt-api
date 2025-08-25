package ma.org.ormt.modules.domaines.domaine.dtos.details;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.summary.SousDomaineSummaryDto;

@Setter
@Getter
@Schema(name = "DomaineDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "domaine.id" }, allowGetters = true)
public class DomaineDetailDto extends DomaineDto {

    // List<SousDomainePublicDto> sousDomaines;
    // List<SousDomaineSummaryDto> sousDomaines;

}
