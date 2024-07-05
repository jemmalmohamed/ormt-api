package ma.org.ancfcc.pva.core.commun.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface BaseService<T> {

    public List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    List<T> findBySpecification(Specification<T> specification);

    public Optional<T> findById(Long id);

    public List<Long> findAllIds();

    T create(@NonNull T entity);

    List<T> saveAll(List<T> entities);

    public void deleteAllById(List<Long> ids);

    public void delete(Long id);

    public void deleteAllExceptIds(List<Long> ids);

    public void deleteAll();

    List<Long> deleteBySpecification(List<String> filters, String globalFilter, Class<T> clazz);

    List<Long> deleteBySpecificationExceptIds(List<String> filters, String globalFilter, Class<T> clazz,
            List<Long> ids);

    public T update(Long id, T entity);

    default void validateBeforeDelete(Long id) {
        // Default implementation (does nothing)
    }

}