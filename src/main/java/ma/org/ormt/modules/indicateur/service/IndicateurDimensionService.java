package ma.org.ormt.modules.indicateur.service;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateur.dto.request.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateur.models.IndicateurDimension;

public interface IndicateurDimensionService extends BaseService<IndicateurDimension> {
    Page<IndicateurDimension> getEntityList(QueryParams requestParams);

    IndicateurDimension create(IndicateurDimensionRequestDto requestDto);

    IndicateurDimension update(Long id, IndicateurDimensionRequestDto indicateurDimensionRequestDto);

    boolean existsById(Long id);
}