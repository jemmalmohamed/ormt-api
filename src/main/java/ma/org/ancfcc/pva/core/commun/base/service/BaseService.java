package ma.org.ancfcc.pva.core.commun.base.service;

import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface BaseService<T> {

    public List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    List<T> findBySpecification(Specification<T> specification);

    public Optional<T> findById(UUID id);

    public List<UUID> findAllIds();

    T create(@NonNull T entity);

    List<T> saveAll(List<T> entities);

    public void deleteAllById(List<UUID> ids);

    public void delete(UUID id);

    public void deleteAllExceptIds(List<UUID> ids);

    public void deleteAll();

    List<UUID> deleteBySpecification(List<String> filters, String globalFilter, Class<T> clazz);

    List<UUID> deleteBySpecificationExceptIds(List<String> filters, String globalFilter, Class<T> clazz,
            List<UUID> ids);

    public T update(UUID id, T entity);

    default void validateBeforeDelete(UUID id) {
        // Default implementation (does nothing)
    }

}