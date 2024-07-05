package ma.org.ancfcc.pva.modules.organisme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "Organisme")
@JsonIgnoreProperties(value = { "organisme.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganismeDto extends BaseDto {

    private String nom;

    private String secteur;
}