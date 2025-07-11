package ma.org.ormt.modules.indicateurs.indicateur.services.indicateur;

import java.util.Optional;
import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.request.IndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;

public interface IndicateurService extends BaseService<Indicateur> {

    Optional<Indicateur> findByNom(String nom);

    Page<Indicateur> getEntityList(QueryParams requestParams);

    Indicateur create(IndicateurRequestDto requestDto);

    Indicateur save(Indicateur indicateur);

    Indicateur update(Long id, IndicateurRequestDto indicateurRequestDto);

    boolean existsById(Long id);

    Optional<Indicateur> findByNomWithDonnees(String nom);

    Optional<Indicateur> findByNomWithDonneesAndDimensions(String nom);

    IndicateurDetailDto getIndicateurWithTableData(Long id, String tableFormat);
}