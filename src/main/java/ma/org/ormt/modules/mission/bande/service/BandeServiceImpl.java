package ma.org.ormt.modules.mission.bande.service;

import java.util.Optional;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.gis.utils.GeometryCreation;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.mission.bande.Bande;
import ma.org.ormt.modules.mission.bande.dto.request.BandeRequestDto;
import ma.org.ormt.modules.mission.bande.dto.request.BandeRequestDtoMapper;
import ma.org.ormt.modules.mission.bande.repository.BandeRepository;
import ma.org.ormt.modules.mission.models.Mission;
import ma.org.ormt.modules.mission.service.planification.helper.MissionPlanificationHelper;
import ma.org.ormt.modules.mission.service.planification.xml.parser.PlanLineXmlInfo;

@Service
public class BandeServiceImpl extends BaseServiceImpl<Bande> implements BandeService {

    @Autowired
    private BandeRepository bandeRepository;

    @Autowired
    private ObjectsValidator<BandeRequestDto> validator;

    @Autowired
    private BandeRequestDtoMapper bandeRequestMapper;

    @Autowired
    private MissionPlanificationHelper missionPlanificationHelper;

    public BandeServiceImpl(BandeRepository bandeRepository, SpecificationService specificationService) {
        super(bandeRepository, specificationService);
    }

    public Page<Bande> bandeWithPagination(QueryParams requestParams) {

        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Bande.class)) {

            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Bande> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Bande.class);

        return findAll(specification, pageable);

    }

    /**
     * Finds an Bande by its name
     *
     * @param nom the name of the Bande
     * @return an Optional containing the found Bande, or empty if not found
     */
    @Override
    public Optional<Bande> findByNom(String nom) {
        return bandeRepository.findByNom(nom);
    }

    @Override
    public Optional<Bande> findByLabel(String nom) {
        return bandeRepository.findByLabel(nom);
    }

    /**
     * Updates an Bande with new values
     *
     * @param id           the ID of the Bande to update
     * @param updatedBande the new Bande data
     * @return the updated Bande
     */
    @Override
    // @Transactional
    public Bande update(Long id, BandeRequestDto bandeRequestDto) {
        validator.validate(bandeRequestDto);
        Bande bandeToUpdate = bandeRequestMapper.mapToEntity(bandeRequestDto);

        checkPathId(id, bandeToUpdate.getId());

        Bande bande = bandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bande not found"));

        bande.setNom(bandeToUpdate.getNom());
        bande.setObservation(bandeToUpdate.getObservation());

        return bandeRepository.save(bande);
    }

    @Override
    // @Transactional
    public Bande create(BandeRequestDto requestDto) {

        validator.validate(requestDto);

        Bande bandeToCreate = bandeRequestMapper.mapToEntity(requestDto);

        Bande bande = create(bandeToCreate);

        return bandeRepository.save(bande);
    }

    @Override
    public boolean existsByLabelAndMissionId(String label, Long missionId) {
        return bandeRepository.existsByLabelAndMissionId(label, missionId);
    }

    @Override
    public Optional<Bande> findBandeByLabelAndMissionId(String label, Long missionId) {
        return bandeRepository.findBandeByLabelAndMissionId(label, missionId);
    }

    @Override
    @Transactional
    public void deleteBandesByMissionId(Long missionId) {
        bandeRepository.deleteAllByMissionId(missionId);
    }

    @Override
    public Bande saveBandePlanificationFromShapeFileFeature(String nom, Point start, Point end, Mission mission,
            Integer srid) {
        Bande bande = new Bande();
        bande.setMission(mission);
        bande.setNom(nom);
        bande.setLabel(nom);
        LineString lineString = GeometryCreation.createLineFromPoints(start, end);
        lineString.setSRID(srid);
        bande.setAxePlanification(lineString);
        return create(bande);
    }

    @Override
    public Bande saveBandePlanificationFromXml(PlanLineXmlInfo planLine, Mission mission, Integer srid) {
        Bande bande = new Bande();
        bande.setMission(mission);
        bande.setNom(missionPlanificationHelper.formaLabel(planLine.getPlanLineLabel()));
        bande.setLabel(planLine.getPlanLineLabel());
        LineString lineString = GeometryCreation.createLineString(planLine.getStartPosition(),
                planLine.getEndPosition());
        bande.setAxePlanification(lineString);
        lineString.setSRID(srid);
        return create(bande);
    }

}