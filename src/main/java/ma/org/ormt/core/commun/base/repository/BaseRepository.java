package ma.org.ormt.core.commun.base.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import jakarta.transaction.Transactional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    @Modifying
    @Transactional
    @Query("update #{#entityName} t SET t.statusCode = :statusCode WHERE t.id = :id")
    void updateStatus(@Param("id") Long id, @Param("statusCode") Integer statusCode);

    boolean existsById(@NonNull Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id NOT IN :ids")
    void deleteAllExceptIds(@Param("ids") List<Long> ids);

    @Query("SELECT t.id FROM #{#entityName} t WHERE t.id NOT IN :ids")
    List<Long> findAllIdsNotIn(@Param("ids") List<Long> ids);

    @Query("SELECT t.id FROM #{#entityName} t")
    List<Long> findAllIds();

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id IN :ids")
    void deleteAllById(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("delete from #{#entityName}")
    void deleteAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t WHERE t.id = :id")
    void deleteById(@Param("id") @NonNull Long id);

}