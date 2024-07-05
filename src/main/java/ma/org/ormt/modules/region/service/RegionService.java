package ma.org.ormt.modules.region.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.region.Region;
import ma.org.ormt.modules.region.dto.request.RegionRequestDto;

public interface RegionService extends BaseService<Region> {

    Optional<Region> findByNom(String nom);

    Page<Region> getEntityList(QueryParams requestParams);

    Region create(RegionRequestDto requestDto);

    Region update(Long id, RegionRequestDto regionRequestDto);

    boolean existsById(Long id);

    void createRegionFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException;

}