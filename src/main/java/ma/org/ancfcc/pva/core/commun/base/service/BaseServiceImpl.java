package ma.org.ancfcc.pva.core.commun.base.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;

@MappedSuperclass
@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity<ID>, ID extends Number> implements BaseService<T, ID> {

    protected final BaseRepository<T, ID> baseRepository;

    protected final SpecificationService specificationService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<ID> findAllIds() {
        return this.baseRepository.findAllIds();

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
    public Optional<T> findById(ID id) {
        return baseRepository.findById(id);
    }

    // ** UPDATE **//
    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public T update(ID id, T entity) {
        checkPathId(id, entity.getId());
        return baseRepository.save(entity);
    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public void deleteAllById(List<ID> ids) {

        validateEntitiesBeforeDelete(ids);
        baseRepository.deleteAllById(ids);

    }

    @Transactional
    public void delete(ID id) {

        validateEntitiesBeforeDelete(List.of(id));

        baseRepository.deleteById(id);

    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Override
    @Transactional
    public void deleteAll() {

        List<ID> ids = baseRepository.findAllIds();

        validateEntitiesBeforeDelete(ids);

        baseRepository.deleteAllById(ids);

    }

    // @CacheEvict(cacheResolver = "dynamicCacheNameResolver", allEntries = true)
    @Transactional
    public void deleteAllExceptIds(List<ID> ids) {

        List<ID> idsToDelete = baseRepository.findAllIdsNotIn(ids);

        validateEntitiesBeforeDelete(idsToDelete);

        baseRepository.deleteAllById(idsToDelete);

    }

    public List<T> findBySpecification(Specification<T> specification) {
        return baseRepository.findAll(specification);
    }

    @Override
    @Transactional
    public List<ID> deleteBySpecification(List<String> filters, String globalFilter, Class<T> clazz) {

        Specification<T> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(filters, globalFilter, clazz);

        List<T> entities = findBySpecification(specification);

        List<ID> ids = entities.stream().map(T::getId).collect(Collectors.toList());

        deleteAllById(ids);

        return ids;
    }

    @Override
    @Transactional
    public List<ID> deleteBySpecificationExceptIds(List<String> filters, String globalFilter, Class<T> clazz,
            List<ID> exceptIds) {

        Specification<T> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(filters, globalFilter, clazz);

        List<T> entities = findBySpecification(specification);

        entities.removeIf(entity -> exceptIds.contains(entity.getId()));

        List<ID> ids = entities.stream().map(T::getId).collect(Collectors.toList());

        deleteAllById(ids);

        return ids;
    }

    /**
     * Check if entity id is the same as path id
     * 
     * @param id
     * @param pathId
     */
    public void checkPathId(ID id, ID pathId) {
        if (!id.equals(pathId)) {
            String message = MessageResponse.builder().title("Erreur")
                    .mainMessage("L'identifiant dans le chemin et dans le corps de la requête ne sont pas les mêmes")
                    .build()
                    .format();
            throw new IllegalArgumentException(message);
        }
    }

    private void validateEntitiesBeforeDelete(List<ID> ids) {
        for (ID id : ids) {
            validateBeforeDelete(id);
        }
    }

}