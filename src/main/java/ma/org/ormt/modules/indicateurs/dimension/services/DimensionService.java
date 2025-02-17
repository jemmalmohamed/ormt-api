package ma.org.ormt.modules.indicateurs.dimension.services;

import java.util.Optional;
import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.dimension.dtos.request.DimensionRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;

public interface DimensionService extends BaseService<Dimension> {

    Optional<Dimension> findByNom(String nom);

    Page<Dimension> getEntityList(QueryParams requestParams);

    Dimension create(DimensionRequestDto requestDto);

    Dimension update(Long id, DimensionRequestDto dimensionRequestDto);

    Dimension save(Dimension dimension);

    boolean existsById(Long id);

    void associateWithIndicateur(Long dimensionId, Long indicateurId);

    void dissociateFromIndicateur(Long dimensionId, Long indicateurId);
}