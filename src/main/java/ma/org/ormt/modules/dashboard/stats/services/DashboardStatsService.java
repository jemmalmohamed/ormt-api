package ma.org.ormt.modules.dashboard.stats.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.dashboard.stats.dtos.DashboardStatsDto;
import ma.org.ormt.modules.dashboard.tableaubord.repositories.TableauBordRepository;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.source.repositories.SourceRepository;
import ma.org.ormt.modules.publications.publication.repositories.PublicationRepository;

@Service
@RequiredArgsConstructor
public class DashboardStatsService {

    private final EspaceRepository espaceRepository;
    private final DomaineRepository domaineRepository;
    private final SousDomaineRepository sousDomaineRepository;
    private final IndicateurRepository indicateurRepository;
    private final PublicationRepository publicationRepository;
    private final SourceRepository sourceRepository;
    private final TableauBordRepository tableauBordRepository;
    private final ChiffreCleRepository chiffreCleRepository;

    public DashboardStatsDto getStats() {
        return DashboardStatsDto.builder()
                .espaces(espaceRepository.count())
                .domaines(domaineRepository.count())
                .sousDomaines(sousDomaineRepository.count())
                .indicateurs(indicateurRepository.count())
                .publications(publicationRepository.count())
                .sources(sourceRepository.count())
                .tableauxBord(tableauBordRepository.count())
                .chiffresCles(chiffreCleRepository.count())
                .build();
    }
}
