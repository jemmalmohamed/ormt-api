package ma.org.ormt.modules.dashboard.stats.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long espaces;
    private long domaines;
    private long sousDomaines;
    private long indicateurs;
    private long publications;
    private long sources;
    private long tableauxBord;
    private long chiffresCles;
}
