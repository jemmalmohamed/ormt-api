package ma.org.ormt.modules.espaces.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.repository.EspaceDomaineRepository;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;
import ma.org.ormt.modules.espaces.services.EspaceService;

@Service
@Transactional
public class EspaceServiceImpl extends BaseServiceImpl<Espace> implements EspaceService {

    @Autowired
    private EspaceDomaineRepository espaceDomaineRepository;
    @Autowired
    private EspaceRepository espaceRepository;
    @Autowired
    private DomaineService domaineService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectsValidator<EspaceRequestDto> validator;

    @Autowired
    private EspaceRequestDtoMapper espaceRequestMapper;

    private static final String NOT_FOUND_STRING = "Espace non trouvée";

    public EspaceServiceImpl(EspaceRepository espaceRepository, SpecificationService specificationService) {
        super(espaceRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return espaceRepository.existsById(id);
    }

    @Override
    public Optional<Espace> findByNom(String nom) {
        return espaceRepository.findByNom(nom);
    }

    @Override
    public Page<Espace> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Espace.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Espace> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Espace.class);
        return findAll(specification, pageable);
    }

    @Override
    public Page<Espace> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Espace.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        // If no IDs are provided or empty list, return empty page
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // Create specification for filtering by IDs
        Specification<Espace> idSpecification = (root, _, _) -> root.get("id").in(ids);

        // Get filter specification and handle null case
        Specification<Espace> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Espace.class);

        // Combine specifications, handling null case
        Specification<Espace> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    @Override
    public Espace save(Espace espace) {
        return espaceRepository.save(espace);
    }

    @Override
    public Espace create(EspaceRequestDto requestDto) {
        try {
            validator.validate(requestDto);
            String imageFileName = minioService.uploadFile(requestDto.getImageFile());

            Espace espaceToCreate = espaceRequestMapper.mapToEntity(requestDto);
            espaceToCreate.setImageUrl(imageFileName); // Store just the filename
            return espaceRepository.save(espaceToCreate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la création de la espace: " + e.getMessage());
        }
    }

    @Override
    public Espace update(Long id, EspaceRequestDto requestDto) {
        try {
            validator.validate(requestDto);

            Espace espaceToUpdate = espaceRequestMapper.mapToEntity(requestDto);

            checkPathId(id, espaceToUpdate.getId());

            Espace espace = espaceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

            espace.setNom(requestDto.getNom());
            espace.setDescription(requestDto.getDescription());
            espace.setApropos(requestDto.getApropos());
            espace.setStatut(requestDto.getStatut());
            try {
                if (requestDto.getImageFile() != null && !requestDto.getImageFile().isEmpty()) {

                    // Upload the new file
                    String imageFileName = minioService.uploadFile(requestDto.getImageFile());

                    // Store just the filename, not the full URL
                    espace.setImageUrl(imageFileName);
                }
                // If no new photo is provided, keep the existing one
            } catch (Exception e) {
                throw new RuntimeException("Error updating partenaire photo: " + e.getMessage(), e);
            }
            return espaceRepository.save(espace);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la mise à jour de la espace: " + e.getMessage());
        }
    }

    public void attachDomaine(Long espaceId, Long domaineId) {
        Espace espace = espaceRepository.findById(espaceId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        Domaine domaine = domaineService.findById(domaineId)
                .orElseThrow(() -> new EntityNotFoundException("Domaine non trouvé"));

        EspaceDomaine espaceDomaine = new EspaceDomaine();
        espaceDomaine.setEspace(espace);
        espaceDomaine.setDomaine(domaine);

        espaceDomaineRepository.save(espaceDomaine);
    }

    public void detachDomaine(Long espaceDomaineId) {

        EspaceDomaine espaceDomaine = espaceDomaineRepository.findById(espaceDomaineId)
                .orElseThrow(() -> new EntityNotFoundException("Association non trouvée"));

        espaceDomaineRepository.delete(espaceDomaine);
    }

}