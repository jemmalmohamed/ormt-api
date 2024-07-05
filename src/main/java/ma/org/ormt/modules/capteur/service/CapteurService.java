package ma.org.ormt.modules.capteur.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.capteur.Capteur;
import ma.org.ormt.modules.capteur.dto.request.CapteurRequestDto;

public interface CapteurService extends BaseService<Capteur> {

    Optional<Capteur> findByNom(String nom);

    Optional<Capteur> findByCode(String code);

    Page<Capteur> getEntityList(QueryParams requestParams);

    Capteur create(CapteurRequestDto requestDto);

    String getCapteurCodeFromString(String nom);

    Capteur update(Long id, CapteurRequestDto capteurRequestDto);

    boolean existsById(Long id);

}