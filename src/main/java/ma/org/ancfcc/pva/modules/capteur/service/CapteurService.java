package ma.org.ancfcc.pva.modules.capteur.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestDto;

public interface CapteurService extends BaseService<Capteur> {

    Optional<Capteur> findByNom(String nom);

    Optional<Capteur> findByCode(String code);

    Page<Capteur> getEntityList(QueryParams requestParams);

    Capteur create(CapteurRequestDto requestDto);

    Capteur update(UUID id, CapteurRequestDto capteurRequestDto);

    boolean existsById(UUID id);

}