package ma.org.ormt.modules.dashboard.tbd.categories.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdCategoryDto extends BaseDto {

    private String nom;

    private String libelle;

    private String description;

    private Integer ordre;

    private Boolean actif;

    private Long tbDomaineId;

    private TBDomaineDto tbDomaine;
}
