package ma.org.ormt.modules.region.service;

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
import ma.org.ormt.modules.region.Region;
import ma.org.ormt.modules.region.dto.request.RegionRequestDto;
import ma.org.ormt.modules.region.dto.request.RegionRequestMapper;
import ma.org.ormt.modules.region.repository.RegionRepository;

@Log4j2
@Service
public class RegionServiceImpl extends BaseServiceImpl<Region> implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ObjectsValidator<RegionRequestDto> validator;

    @Autowired
    private RegionRequestMapper regionRequestMapper;

    static final String NOT_FOUND_STRING = "Region not found";

    public RegionServiceImpl(RegionRepository regionRepository, SpecificationService specificationService) {
        super(regionRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return regionRepository.existsById(id);
    }

    @Override
    public Optional<Region> findByNom(String nom) {
        return regionRepository.findByNom(nom);
    }

    @Override
    public Page<Region> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Region.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Region> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Region.class);
        return findAll(specification, pageable);
    }

    @Override
    public Region create(RegionRequestDto requestDto) {
        validator.validate(requestDto);
        Region regionToCreate = regionRequestMapper.mapToEntity(requestDto);
        return regionRepository.save(regionToCreate);
    }

    @Override
    public Region update(Long id, RegionRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Region regionToUpdate = regionRequestMapper.mapToEntity(requestDto);
        checkPathId(id, regionToUpdate.getId());
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(region, regionToUpdate);
        return regionRepository.save(region);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateRegionDependencies(id);
    }

    private void updateFields(Region region, Region entityToUpdate) {
        region.setNom(entityToUpdate.getNom());
        region.setDescription(entityToUpdate.getDescription());

    }

    private void validateRegionDependencies(Long id) {

    }

    @Override
    @Transactional
    public void createRegionFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException {

        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "prj");

        ShpFileService.validatePrjFileIsWgs(prjFile, srid);

        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "shp");

        SimpleFeatureCollection featureCollection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        try (SimpleFeatureIterator features = featureCollection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                try {

                    Region region = createRegionFromSimpleFeature(feature, srid);
                    if (regionRepository.existsByNom(region.getNom())) {
                        log.info("Region with code: " + region.getNom() + " already exists");

                    } else {
                        create(region);

                        // regionGsFeatureService.updateRegionTableFeatureType(gsWorkspace,
                        // gsPostgis_datastore);

                    }
                } catch (Exception e) {
                    log.error("Error creating region from shapefile", e);
                    String message = MessageResponse.builder()
                            .title("Invalide Shapefile")
                            .mainMessage("Le shapefile ne contient pas les attributs d'une region")
                            .subMessage("Veuillez vérifier les attributs de la region")
                            .build()
                            .format();
                    throw new ShapefileProcessingException(message);
                }

            }

        }

    }

    public Region createRegionFromSimpleFeature(SimpleFeature feature, Integer srid) {

        String regionName = ShpSimpleFeatureService.getValueFromFeature(feature, "region");
        String superficie = ShpSimpleFeatureService.getValueFromFeature(feature, "superficie");

        Region region = new Region();

        region.setNom(regionName.toLowerCase());
        if (!superficie.equals("")) {
            region.setSuperficie(
                    Long.parseLong(superficie));
        } else {
            region.setSuperficie(0L);
        }
        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);
            if (geometry instanceof MultiPolygon) {
                MultiPolygon delimitation = (MultiPolygon) geometry;
                delimitation.setSRID(srid);
                if (delimitation.isValid()) {
                    region.setDelimitation(delimitation);
                } else {
                    throw new IllegalArgumentException(
                            "Delimitation de la region " + region.getNom() + " n'est pas valide");
                }

            }
        }

        return region;
    }

}