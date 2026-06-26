package ma.org.ormt.modules.dashboard.tbd.dtos;

import java.util.List;

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
public class TbdDashboardFullDto {

    private Long id;
    private String nom;
    private String titre;
    private String sousTitre;
    private String description;
    private String sourceText;
    private Boolean actif;
    private String status;
    private TbdAssignationDto assignation;
    private List<TbdSourceDto> sources;
    private List<TbdSectionDto> sections;
}
