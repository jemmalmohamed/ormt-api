package ma.org.ormt.modules.indicateurs.indicateur.dtos.link;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurLinkedDashboardDto {

    private Long id;
    private String nom;
    private String titre;
    private String status;
    private Boolean actif;
    private LocalDateTime lastModifiedDate;
    private Long categorieAnalytiqueId;
    private String categorieAnalytiqueLibelle;
}
