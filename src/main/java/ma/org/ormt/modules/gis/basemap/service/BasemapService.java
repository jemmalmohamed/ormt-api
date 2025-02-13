package ma.org.ormt.modules.gis.basemap.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.gis.basemap.Basemap;
import ma.org.ormt.modules.gis.basemap.dto.request.BasemapRequestDto;

public interface BasemapService extends BaseService<Basemap> {

    Optional<Basemap> findByNom(String nom);

    Page<Basemap> getEntityList(QueryParams requestParams);

    Basemap create(BasemapRequestDto requestDto);

    Basemap update(Long id, BasemapRequestDto basemapRequestDto);

    boolean existsById(Long id);

}