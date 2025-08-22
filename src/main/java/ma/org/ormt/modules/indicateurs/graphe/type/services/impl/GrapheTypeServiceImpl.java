package ma.org.ormt.modules.indicateurs.graphe.type.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.graphe.type.repositories.GrapheTypeRepository;
import ma.org.ormt.modules.indicateurs.graphe.type.services.GrapheTypeService;

@Service
@Transactional
public class GrapheTypeServiceImpl extends BaseServiceImpl<GrapheType> implements GrapheTypeService {

    @Autowired
    private GrapheTypeRepository graphetypeRepository;

    public GrapheTypeServiceImpl(GrapheTypeRepository graphetypeRepository, SpecificationService specificationService) {
        super(graphetypeRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return graphetypeRepository.existsById(id);
    }

    @Override
    public Optional<GrapheType> findByNom(String nom) {
        return graphetypeRepository.findByNom(nom);
    }

    @Override
    public Optional<GrapheType> findByCode(String code) {
        return graphetypeRepository.findByCode(code);
    }

    @Override
    public List<GrapheType> findByCodeIn(Collection<String> codes) {
        return graphetypeRepository.findByCodeIn(codes);
    }

    @Override
    public Page<GrapheType> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), GrapheType.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<GrapheType> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), GrapheType.class);
        return findAll(specification, pageable);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateGrapheTypeDependencies(id);
    }

    private void validateGrapheTypeDependencies(Long id) {

    }

}