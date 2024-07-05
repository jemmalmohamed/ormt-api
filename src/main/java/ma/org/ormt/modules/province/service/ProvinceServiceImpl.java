package ma.org.ormt.modules.province.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ormt.core.gis.shapefile.ShpFileService;
import ma.org.ormt.core.gis.shapefile.ShpSimpleFeatureService;
import ma.org.ormt.core.gis.utils.GeometryConversion;
import ma.org.ormt.core.gis.utils.GeometryUtils;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.FileUtils;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.province.Province;
import ma.org.ormt.modules.province.dto.request.ProvinceRequestDto;
import ma.org.ormt.modules.province.dto.request.ProvinceRequestMapper;
import ma.org.ormt.modules.province.repository.ProvinceRepository;
import ma.org.ormt.modules.region.Region;
import ma.org.ormt.modules.region.repository.RegionRepository;

@Log4j2
@Service
public class ProvinceServiceImpl extends BaseServiceImpl<Province> implements ProvinceService {

    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ObjectsValidator<ProvinceRequestDto> validator;

    @Autowired
    private ProvinceRequestMapper provinceRequestMapper;

    static final String NOT_FOUND_STRING = "Province not found";

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, SpecificationService specificationService) {
        super(provinceRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return provinceRepository.existsById(id);
    }

    @Override
    public Optional<Province> findByNom(String nom) {
        return provinceRepository.findByNom(nom);
    }

    @Override
    public Page<Province> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Province.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Province> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Province.class);
        return findAll(specification, pageable);
    }

    @Override
    public Province create(ProvinceRequestDto requestDto) {
        validator.validate(requestDto);
        Province provinceToCreate = provinceRequestMapper.mapToEntity(requestDto);
        return provinceRepository.save(provinceToCreate);
    }

    @Override
    public Province update(Long id, ProvinceRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Province provinceToUpdate = provinceRequestMapper.mapToEntity(requestDto);
        checkPathId(id, provinceToUpdate.getId());
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(province, provinceToUpdate);
        return provinceRepository.save(province);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateProvinceDependencies(id);
    }

    private void updateFields(Province province, Province entityToUpdate) {
        province.setNom(entityToUpdate.getNom());
        province.setDescription(entityToUpdate.getDescription());

    }

    private void validateProvinceDependencies(Long id) {

    }

    @Override
    @Transactional
    public void createProvinceFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException {

        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "prj");

        ShpFileService.validatePrjFileIsWgs(prjFile, srid);

        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "shp");

        SimpleFeatureCollection featureCollection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        try (SimpleFeatureIterator features = featureCollection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                try {

                    Province province = createProvinceFromSimpleFeature(feature, srid);

                    if (provinceRepository.existsByNom(province.getNom())) {
                        log.info("Province with code: " + province.getNom() + " already exists");

                    } else {
                        create(province);

                    }
                } catch (Exception e) {
                    log.error("Error creating province from shapefile", e);
                    String message = MessageResponse.builder()
                            .title("Invalide Shapefile")
                            .mainMessage("Le shapefile ne contient pas les attributs d'une province")
                            .subMessage("Veuillez vérifier les attributs de la province")
                            .build()
                            .format();
                    throw new ShapefileProcessingException(message);
                }

            }

        }

    }

    public Province createProvinceFromSimpleFeature(SimpleFeature feature, Integer srid) {

        String regionName = ShpSimpleFeatureService.getValueFromFeature(feature, "region");
        String provinceName = ShpSimpleFeatureService.getValueFromFeature(feature, "province");
        String superficie = ShpSimpleFeatureService.getValueFromFeature(feature, "superficie");
        String type = ShpSimpleFeatureService.getValueFromFeature(feature, "type");

        Province province = new Province();

        province.setNom(provinceName.toLowerCase());
        province.setType_collectivite(type.toLowerCase());

        Region region = regionRepository.findByNom(regionName.toLowerCase()).get();

        province.setRegion(region);

        if (!superficie.equals("")) {
            province.setSuperficie(
                    Long.parseLong(superficie));
        } else {
            province.setSuperficie(0L);
        }

        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);
            if (geometry instanceof MultiPolygon) {
                MultiPolygon delimitation = (MultiPolygon) geometry;
                delimitation.setSRID(srid);
                if (delimitation.isValid()) {
                    province.setDelimitation(delimitation);
                } else {
                    throw new IllegalArgumentException(
                            "Delimitation de la province " + province.getNom() + " n'est pas valide");
                }

            }
        }

        return province;
    }

}