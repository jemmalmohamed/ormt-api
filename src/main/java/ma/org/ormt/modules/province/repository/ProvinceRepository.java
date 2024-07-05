package ma.org.ormt.modules.province.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.province.Province;

public interface ProvinceRepository extends BaseRepository<Province> {

    Optional<Province> findByNom(String nom);

    boolean existsByNom(String code);

    boolean existsByIdAndDelimitationIsNotNull(Long id);

}