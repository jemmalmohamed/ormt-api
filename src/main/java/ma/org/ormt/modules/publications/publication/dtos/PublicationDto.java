package ma.org.ormt.modules.publications.publication.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.AdminRoleFilter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Schema(name = "Publication")
@JsonIgnoreProperties(value = { "publication.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class PublicationDto extends BaseDto {

    private String titre;

    private String description;

    private String auteur;

    private LocalDate datePublication;

    private String fichierUrl;

    private String nomFichier;

    private boolean actif;

    private Long tailleFichier;

    private String categorie;

    private String tags;

    private Integer nombreTelechargements;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;

}
