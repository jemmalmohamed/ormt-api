package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2SourceRefDto extends Dto {

    private String nom;

    private String titre;

    private String libelle;

    private String valeur;

    private String unite;
}
