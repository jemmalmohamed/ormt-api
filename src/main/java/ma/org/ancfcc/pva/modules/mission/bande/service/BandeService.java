package ma.org.ancfcc.pva.modules.mission.bande.service;

import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.bande.dto.request.BandeRequestDto;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.PlanLineXmlInfo;

public interface BandeService extends BaseService<Bande> {

    Optional<Bande> findByNom(String nom);

    Optional<Bande> findByLabel(String label);

    Optional<Bande> findBandeByLabelAndMissionId(String label, Long missionId);

    void deleteBandesByMissionId(Long missionId);

    Page<Bande> bandeWithPagination(QueryParams requestParams);

    Bande create(BandeRequestDto requestDto);

    Bande update(Long id, BandeRequestDto bandeRequestDto);

    boolean existsByLabelAndMissionId(String label, Long missionId);

    Bande saveBandePlanificationFromShapeFileFeature(String nom, Point start, Point end, Mission mission,
            Integer srid);

    Bande saveBandePlanificationFromXml(PlanLineXmlInfo planLine, Mission mission, Integer srid);

}