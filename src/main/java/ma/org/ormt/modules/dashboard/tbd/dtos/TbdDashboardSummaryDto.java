package ma.org.ormt.modules.dashboard.tbd.dtos;

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
public class TbdDashboardSummaryDto {

    private Long id;
    private String nom;
    private String titre;
    private String sousTitre;
    private String status;
    private Boolean actif;
    private Long categorieAnalytiqueId;
    private String assignationNom;
    private Integer nbSections;
    private LocalDateTime lastModifiedDate;
}
