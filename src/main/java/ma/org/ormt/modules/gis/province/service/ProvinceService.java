package ma.org.ormt.modules.gis.province.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.gis.province.Province;
import ma.org.ormt.modules.gis.province.dto.request.ProvinceRequestDto;

public interface ProvinceService extends BaseService<Province> {

    Optional<Province> findByNom(String nom);

    Page<Province> getEntityList(QueryParams requestParams);

    Province create(ProvinceRequestDto requestDto);

    Province update(Long id, ProvinceRequestDto provinceRequestDto);

    boolean existsById(Long id);

    void createProvinceFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException;

}