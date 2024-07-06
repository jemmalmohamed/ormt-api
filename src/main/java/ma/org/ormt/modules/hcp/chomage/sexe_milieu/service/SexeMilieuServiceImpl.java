package ma.org.ormt.modules.hcp.chomage.sexe_milieu.service;

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
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.SexeMilieu;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.request.SexeMilieuRequestDto;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.request.SexeMilieuRequestMapper;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.repository.SexeMilieuRepository;

@Service
public class SexeMilieuServiceImpl extends BaseServiceImpl<SexeMilieu> implements SexeMilieuService {

    @Autowired
    private SexeMilieuRepository sexeMilieuRepository;

    @Autowired
    private ObjectsValidator<SexeMilieuRequestDto> validator;

    @Autowired
    private SexeMilieuRequestMapper sexeMilieuRequestMapper;

    static final String NOT_FOUND_STRING = "SexeMilieu not found";

    public SexeMilieuServiceImpl(SexeMilieuRepository sexeMilieuRepository, SpecificationService specificationService) {
        super(sexeMilieuRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return sexeMilieuRepository.existsById(id);
    }

    @Override
    public Optional<SexeMilieu> findByNom(String nom) {
        return sexeMilieuRepository.findByAnnee(nom);
    }

    @Override
    public Page<SexeMilieu> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), SexeMilieu.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<SexeMilieu> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), SexeMilieu.class);
        return findAll(specification, pageable);
    }

    @Override
    public SexeMilieu create(SexeMilieuRequestDto requestDto) {
        validator.validate(requestDto);
        SexeMilieu sexeMilieuToCreate = sexeMilieuRequestMapper.mapToEntity(requestDto);
        return sexeMilieuRepository.save(sexeMilieuToCreate);
    }

    @Override
    public SexeMilieu update(Long id, SexeMilieuRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        SexeMilieu sexeMilieuToUpdate = sexeMilieuRequestMapper.mapToEntity(requestDto);
        checkPathId(id, sexeMilieuToUpdate.getId());
        SexeMilieu sexeMilieu = sexeMilieuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(sexeMilieu, sexeMilieuToUpdate);
        return sexeMilieuRepository.save(sexeMilieu);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateSexeMilieuDependencies(id);
    }

    private void updateFields(SexeMilieu sexeMilieu, SexeMilieu entityToUpdate) {
        sexeMilieu.setAnnee(entityToUpdate.getAnnee());
        sexeMilieu.setMilieu(entityToUpdate.getMilieu());
        sexeMilieu.setSexe(entityToUpdate.getSexe());
        sexeMilieu.setTaux(entityToUpdate.getTaux());
    }

    private void validateSexeMilieuDependencies(Long id) {

    }

}