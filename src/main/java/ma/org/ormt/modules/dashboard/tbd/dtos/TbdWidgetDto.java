package ma.org.ormt.modules.dashboard.tbd.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbdWidgetDto {

    private Long id;
    private String type;
    private String titre;
    private Integer ordre;
    private Integer sizePercent;
    private Long indicateurId;
    private String indicateurNom;
    private String indicateurTitre;
    private Long kpiId;
    private ChiffreCleDto kpi;
    private String contentJson;
}
