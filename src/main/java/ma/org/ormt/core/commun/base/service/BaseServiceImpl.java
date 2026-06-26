package ma.org.ormt.core.commun.base.service;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.core.commun.base.specification.SpecificationAndPageable;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;

@MappedSuperclass
@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    protected final BaseRepository<T> baseRepository;

    protected final SpecificationService specificationService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Long> findAllIds() {
        return this.baseRepository.findAllIds();

    }

    /**
     * Common existence check by id
     */
    public boolean existsById(Long id) {
        return baseRepository.existsById(id);
    }

    // ** CREATE **//
    @Transactional
    public T create(@NonNull T entity) {
        return baseRepository.save(entity);
    }

    // ** SAVE ALL */
    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    public List<T> saveAll(List<T> entities) {
        return baseRepository.saveAll(entities);
    }

    // ** READ ALL **//
    // @Cacheable(cacheResolver = "dynamicCacheNameResolver")
    public List<T> findAll() {
        return baseRepository.findAll();
    }

    // READ all by ids
    public List<T> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return baseRepository.findAllById(ids);
    }

    // @Cacheable(cacheResolver = "dynamicCacheNameResolver")
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    // @Cacheable(cacheResolver = "dynamicCacheNameResolver", keyGenerator =
    // "allEntitiesKeyGenerator")
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return baseRepository.findAll(spec, pageable);
    }

    // ** READ ONE **//
    public Optional<T> findById(Long id) {
        return baseRepository.findById(id);
    }

    /**
     * Convenience to fetch an entity or throw EntityNotFoundException with a custom message
     */
    public T getOrThrow(Long id, String notFoundMessage) {
        return baseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(notFoundMessage));
    }

    // ** UPDATE **//
    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public T update(Long id, T entity) {
        checkPathId(id, entity.getId());
        return baseRepository.save(entity);
    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public void deleteAllById(List<Long> ids) {

        validateEntitiesBeforeDelete(ids);
        baseRepository.deleteAllById(ids);

    }

    @Transactional
    public void delete(Long id) {

        validateEntitiesBeforeDelete(List.of(id));

        baseRepository.deleteById(id);

    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Override
    @Transactional
    public void deleteAll() {

        List<Long> ids = baseRepository.findAllIds();

        validateEntitiesBeforeDelete(ids);

        baseRepository.deleteAllById(ids);

    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public void deleteAllExceptIds(List<Long> ids) {

        List<Long> idsToDelete = baseRepository.findAllIdsNotIn(ids);

        validateEntitiesBeforeDelete(idsToDelete);

        baseRepository.deleteAllById(idsToDelete);

    }

    public List<T> findBySpecification(Specification<T> specification) {
        return baseRepository.findAll(specification);
    }

    @Override
    @Transactional
    public List<Long> deleteBySpecification(List<String> filters, String globalFilter, Class<T> clazz) {

        Specification<T> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(filters, globalFilter, clazz);

        List<T> entities = findBySpecification(specification);

        List<Long> ids = entities.stream().map(T::getId).collect(Collectors.toList());

        deleteAllById(ids);

        return ids;
    }

    @Override
    @Transactional
    public List<Long> deleteBySpecificationExceptIds(List<String> filters, String globalFilter, Class<T> clazz,
            List<Long> exceptIds) {

        Specification<T> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(filters, globalFilter, clazz);

        List<T> entities = findBySpecification(specification);

        entities.removeIf(entity -> exceptIds.contains(entity.getId()));

        List<Long> ids = entities.stream().map(T::getId).collect(Collectors.toList());

        deleteAllById(ids);

        return ids;
    }

    public <E> SpecificationAndPageable<E> getSpecificationAndPageable(QueryParams requestParams,
            Class<E> entityClass) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), entityClass)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        Specification<E> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), entityClass);

        return new SpecificationAndPageable<>(specification, pageable);
    }

    /**
     * Return a page for the current entity type using QueryParams (common pattern across services)
     */
    public Page<T> getEntityList(QueryParams requestParams, Class<T> entityClass) {
        SpecificationAndPageable<T> sp = getSpecificationAndPageable(requestParams, entityClass);
        return findAll(sp.getSpecification(), sp.getPageable());
    }

    /**
     * Return entities by a set of IDs combined with filters/pagination.
     */
    public Page<T> getEntitiesByIds(List<Long> ids, QueryParams requestParams, Class<T> entityClass) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), entityClass)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Specification<T> idSpecification = (root, _, _) -> root.get("id").in(ids);

        Specification<T> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), entityClass);

        Specification<T> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    public <E> Specification<E> addPredicateToSpecification(Specification<E> specification,
            Specification<E> additionalSpec) {
        if (specification == null) {
            return additionalSpec;
        }
        if (additionalSpec == null) {
            return specification;
        }
        return (root, query, criteriaBuilder) -> {
            var predicate = specification.toPredicate(root, query, criteriaBuilder);
            var additionalPredicate = additionalSpec.toPredicate(root, query, criteriaBuilder);

            if (predicate == null) {
                return additionalPredicate;
            }

            if (additionalPredicate == null) {
                return predicate;
            }

            return criteriaBuilder.and(predicate, additionalPredicate);
        };
    }

    /**
     * Check if entity id is the same as path id
     * 
     * @param id
     * @param pathId
     */
    public void checkPathId(Long id, Long pathId) {
        if (!id.equals(pathId)) {
            String message = MessageResponse.builder().title("Erreur")
                    .mainMessage("L'identifiant dans le chemin et dans le corps de la requête ne sont pas les mêmes")
                    .build()
                    .format();
            throw new IllegalArgumentException(message);
        }
    }

    private void validateEntitiesBeforeDelete(List<Long> ids) {
        for (Long id : ids) {
            validateBeforeDelete(id);
        }
    }

}
