package ma.org.ancfcc.pva.modules.mission.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.dto.request.MissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface MissionService extends BaseService<Mission> {

    Optional<Mission> findByNom(String nom);

    Optional<Mission> findByCode(String code);

    boolean existsById(UUID id);

    Page<Mission> getEntityList(QueryParams requestParams);

    Mission create(MissionRequestDto requestDto);

    Mission update(UUID id, MissionRequestDto missionRequestDto);

}