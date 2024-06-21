package ma.org.ancfcc.pva.core.commun.base.service;

import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface BaseService<T, ID extends UUID> {

    public List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    List<T> findBySpecification(Specification<T> specification);

    public Optional<T> findById(ID id);

    public List<ID> findAllIds();

    T create(@NonNull T entity);

    List<T> saveAll(List<T> entities);

    public void deleteAllById(List<ID> ids);

    public void delete(ID id);

    public void deleteAllExceptIds(List<ID> ids);

    public void deleteAll();

    List<ID> deleteBySpecification(List<String> filters, String globalFilter, Class<T> clazz);

    List<ID> deleteBySpecificationExceptIds(List<String> filters, String globalFilter, Class<T> clazz, List<ID> ids);

    public T update(ID id, T entity);

    default void validateBeforeDelete(ID id) {
        // Default implementation (does nothing)
    }

}