package ma.org.ormt.modules.indicateur.service;

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
import ma.org.ormt.modules.indicateur.dto.request.IndicateurRequestDto;
import ma.org.ormt.modules.indicateur.dto.request.IndicateurRequestDtoMapper;
import ma.org.ormt.modules.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateur.repository.IndicateurRepository;
import ma.org.ormt.modules.sousdomaine.SousDomaine;
import ma.org.ormt.modules.sousdomaine.service.SousDomaineService;

@Service
public class IndicateurServiceImpl extends BaseServiceImpl<Indicateur> implements IndicateurService {

    @Autowired
    private IndicateurRepository indicateurRepository;

    @Autowired
    private SousDomaineService sousDomaineService;

    @Autowired
    private ObjectsValidator<IndicateurRequestDto> validator;

    @Autowired
    private IndicateurRequestDtoMapper indicateurRequestMapper;

    static final String NOT_FOUND_STRING = "Indicateur not found";
    static final String SOUS_DOMAINE_NOT_FOUND = "SousDomaine not found";
    static final String PERIODICITE_NOT_FOUND = "Periodicite not found";

    public IndicateurServiceImpl(IndicateurRepository indicateurRepository, SpecificationService specificationService) {
        super(indicateurRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return indicateurRepository.existsById(id);
    }

    @Override
    public Optional<Indicateur> findByNom(String nom) {
        return indicateurRepository.findByNom(nom);
    }

    @Override
    public Page<Indicateur> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Indicateur.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Indicateur> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Indicateur.class);
        return findAll(specification, pageable);
    }

    @Override
    public Indicateur create(IndicateurRequestDto requestDto) {
        validator.validate(requestDto);

        SousDomaine sousDomaine = sousDomaineService.findById(requestDto.getIdSousDomaine())
                .orElseThrow(() -> new EntityNotFoundException(SOUS_DOMAINE_NOT_FOUND));

        Indicateur indicateurToCreate = indicateurRequestMapper.mapToEntity(requestDto);
        indicateurToCreate.setSousDomaine(sousDomaine);

        return indicateurRepository.save(indicateurToCreate);
    }

    @Override
    public Indicateur update(Long id, IndicateurRequestDto requestDto) {
        validator.validate(requestDto);
        Indicateur indicateurToUpdate = indicateurRequestMapper.mapToEntity(requestDto);
        checkPathId(id, indicateurToUpdate.getId());

        SousDomaine sousDomaine = sousDomaineService.findById(requestDto.getIdSousDomaine())
                .orElseThrow(() -> new EntityNotFoundException(SOUS_DOMAINE_NOT_FOUND));

        Indicateur indicateur = indicateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(indicateur, indicateurToUpdate);
        indicateur.setSousDomaine(sousDomaine);

        return indicateurRepository.save(indicateur);
    }

    private void updateFields(Indicateur indicateur, Indicateur entityToUpdate) {
        indicateur.setNom(entityToUpdate.getNom());
        indicateur.setDescription(entityToUpdate.getDescription());
        indicateur.setAbreviation(entityToUpdate.getAbreviation());
        indicateur.setTypeTb(entityToUpdate.getTypeTb());
        indicateur.setUniteCalcul(entityToUpdate.getUniteCalcul());
        indicateur.setSource(entityToUpdate.getSource());
        indicateur.setRegleCalcul(entityToUpdate.getRegleCalcul());
    }
}