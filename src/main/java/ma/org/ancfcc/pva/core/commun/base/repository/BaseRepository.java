package ma.org.ancfcc.pva.core.commun.base.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    @Modifying
    @Transactional
    @Query("update #{#entityName} t SET t.statusCode = :statusCode WHERE t.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("statusCode") Integer statusCode);

    boolean existsById(@NonNull UUID id);

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id NOT IN :ids")
    void deleteAllExceptIds(@Param("ids") List<UUID> ids);

    @Query("SELECT t.id FROM #{#entityName} t WHERE t.id NOT IN :ids")
    List<UUID> findAllIdsNotIn(@Param("ids") List<UUID> ids);

    @Query("SELECT t.id FROM #{#entityName} t")
    List<UUID> findAllIds();

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id IN :ids")
    void deleteAllById(@Param("ids") List<UUID> ids);

    @Modifying
    @Transactional
    @Query("delete from #{#entityName}")
    void deleteAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id = :id")
    void deleteById(@Param("id") @NonNull UUID id);

}