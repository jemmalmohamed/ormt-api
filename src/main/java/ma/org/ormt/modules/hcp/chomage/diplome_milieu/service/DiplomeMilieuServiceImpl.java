package ma.org.ormt.modules.hcp.chomage.diplome_milieu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.DiplomeMilieu;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.request.DiplomeMilieuRequestDto;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.request.DiplomeMilieuRequestMapper;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.repository.DiplomeMilieuRepository;

@Service
public class DiplomeMilieuServiceImpl extends BaseServiceImpl<DiplomeMilieu> implements DiplomeMilieuService {

    @Autowired
    private DiplomeMilieuRepository diplomeMilieuRepository;

    @Autowired
    private ObjectsValidator<DiplomeMilieuRequestDto> validator;

    @Autowired
    private DiplomeMilieuRequestMapper diplomeMilieuRequestMapper;

    static final String NOT_FOUND_STRING = "DiplomeMilieu not found";

    public DiplomeMilieuServiceImpl(DiplomeMilieuRepository diplomeMilieuRepository,
            SpecificationService specificationService) {
        super(diplomeMilieuRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return diplomeMilieuRepository.existsById(id);
    }

    @Override
    public Optional<DiplomeMilieu> findByNom(String nom) {
        return diplomeMilieuRepository.findByAnnee(nom);
    }

    @Override
    public Page<DiplomeMilieu> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), DiplomeMilieu.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<DiplomeMilieu> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), DiplomeMilieu.class);
        return findAll(specification, pageable);
    }

    @Override
    public DiplomeMilieu create(DiplomeMilieuRequestDto requestDto) {
        validator.validate(requestDto);
        DiplomeMilieu diplomeMilieuToCreate = diplomeMilieuRequestMapper.mapToEntity(requestDto);
        return diplomeMilieuRepository.save(diplomeMilieuToCreate);
    }

    @Override
    public DiplomeMilieu update(Long id, DiplomeMilieuRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        DiplomeMilieu diplomeMilieuToUpdate = diplomeMilieuRequestMapper.mapToEntity(requestDto);
        checkPathId(id, diplomeMilieuToUpdate.getId());
        DiplomeMilieu diplomeMilieu = diplomeMilieuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(diplomeMilieu, diplomeMilieuToUpdate);
        return diplomeMilieuRepository.save(diplomeMilieu);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDiplomeMilieuDependencies(id);
    }

    private void updateFields(DiplomeMilieu diplomeMilieu, DiplomeMilieu entityToUpdate) {
        diplomeMilieu.setAnnee(entityToUpdate.getAnnee());
        diplomeMilieu.setMilieu(entityToUpdate.getMilieu());
        diplomeMilieu.setDiplome(entityToUpdate.getDiplome());
        diplomeMilieu.setTaux(entityToUpdate.getTaux());
    }

    private void validateDiplomeMilieuDependencies(Long id) {

    }

}