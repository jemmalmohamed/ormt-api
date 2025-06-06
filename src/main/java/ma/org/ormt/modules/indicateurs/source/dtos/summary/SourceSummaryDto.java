package ma.org.ormt.modules.indicateurs.source.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "sourceSummaryDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "source.id" }, allowGetters = true)
public class SourceSummaryDto extends Dto {

    private String nom;

    private String abreviation;

}