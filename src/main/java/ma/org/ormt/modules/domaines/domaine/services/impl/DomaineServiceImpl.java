package ma.org.ormt.modules.domaines.domaine.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.utilities.files.ImageUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDtoMapper;
import ma.org.ormt.modules.domaines.domaine.dtos.details.DomaineDetailDto;
import ma.org.ormt.modules.domaines.domaine.dtos.details.DomaineDetailDtoMapper;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.publicdto.SousDomainePublicDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
public class DomaineServiceImpl extends BaseServiceImpl<Domaine> implements DomaineService {

    @Autowired
    private DomaineRepository domaineRepository;
    @Autowired
    private SousDomaineRepository sousDomaineRepository;

    @Autowired
    private ObjectsValidator<DomaineRequestDto> validator;

    @Autowired
    private MinioService minioService;

    @Autowired
    private DomaineRequestDtoMapper domaineRequestMapper;

    @Autowired
    private DomaineDetailDtoMapper domaineDetailDtoMapper;

    @Autowired
    private IndicateurService indicateurService;

    static final String NOT_FOUND_STRING = "Domaine not found";

    public DomaineServiceImpl(DomaineRepository domaineRepository, SpecificationService specificationService) {
        super(domaineRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return domaineRepository.existsById(id);
    }

    @Override
    public Optional<Domaine> findByNom(String nom) {
        return domaineRepository.findByNom(nom);
    }

    @Override
    public Page<Domaine> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Domaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Domaine> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Domaine.class);
        return findAll(specification, pageable);
    }

    @Override
    public Page<Domaine> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Domaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        // If no IDs are provided or empty list, return empty page
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // Create specification for filtering by IDs
        Specification<Domaine> idSpecification = (root, _, _) -> root.get("id").in(ids);

        // Get filter specification and handle null case
        Specification<Domaine> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Domaine.class);

        // Combine specifications, handling null case
        Specification<Domaine> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    @Override
    public Domaine create(DomaineRequestDto requestDto) throws Exception {

        validator.validate(requestDto);
        MultipartFile optimizedImage = requestDto.getImageFile();
        if (optimizedImage != null && !optimizedImage.isEmpty()) {
            optimizedImage = ImageUtils.optimizeImageWithConverter(
                    optimizedImage, 1024, 1024, 0.8);
        }
        String imageFileName = minioService.uploadFile(optimizedImage);
        Domaine domaineToCreate = domaineRequestMapper.mapToEntity(requestDto);
        domaineToCreate.setImageUrl(imageFileName); // Store just the filename
        return domaineRepository.save(domaineToCreate);

    }

    @Override
    public Domaine update(Long id, DomaineRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        Domaine domaine = domaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateDomaineFields(domaine, requestDto);
        handleImageUpdate(domaine, requestDto);
        return domaineRepository.save(domaine);
    }

    @Override
    public void addSousDomaine(Long domaineId, Long sousDomaineId) {
        Domaine domaine = domaineRepository.findById(domaineId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        SousDomaine sousDomaine = sousDomaineRepository.findById(sousDomaineId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        domaine.getSousDomaines().add(sousDomaine);
        domaineRepository.save(domaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDomaineDependencies(id);
    }

    private void updateDomaineFields(Domaine domaine, DomaineRequestDto entityToUpdate) {
        domaine.setNom(entityToUpdate.getNom());
        domaine.setDescription(entityToUpdate.getDescription());
        domaine.setActif(entityToUpdate.getActif());
        domaine.setApropos(entityToUpdate.getApropos());

    }

    private void handleImageUpdate(Domaine domaine, DomaineRequestDto dto) throws Exception {
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            MultipartFile optimizedImage = ImageUtils.optimizeImageWithConverter(
                    dto.getImageFile(), 1024, 1024, 0.8);
            String imageFileName = minioService.uploadFile(optimizedImage);
            domaine.setImageUrl(imageFileName);
        }
    }

    private void validateDomaineDependencies(Long id) {
        Domaine domaine = domaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        if (!domaine.getSousDomaines().isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer le domaine  car il est associé aux sous domaines.")

                    .build()
                    .format();

            throw new DependencyException(message);

        }
    }

    @Override
    public boolean existsInEspace(Long domaineId, Long espaceId) {
        return domaineRepository.existsByIdAndEspaceDomainesEspaceId(domaineId, espaceId);
    }

    @Override
    public List<Long> getDomaineIdsByEspaceId(Long espaceId) {
        return domaineRepository.findIdsByEspaceId(espaceId);
    }

    @Override
    public DomaineDetailDto getDomaineWithTableData(Long id, String tableFormat) {
        Domaine domaine = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Domaine not found with id: " + id));

        // Map to DTO first
        DomaineDetailDto dto = domaineDetailDtoMapper.mapToDto(domaine);

        // Now enhance each indicateur with table data
        if (tableFormat != null && !tableFormat.isEmpty() && dto.getSousDomaines() != null) {
            for (SousDomainePublicDto sousDomaineDto : dto.getSousDomaines()) {
                if (sousDomaineDto.getIndicateurs() != null) {
                    for (IndicateurDetailDto indicateurDto : sousDomaineDto.getIndicateurs()) {
                        // Get the full indicateur with table data
                        IndicateurDetailDto indicateurWithTableData = indicateurService
                                .getIndicateurWithTableData(indicateurDto.getId(), tableFormat);

                        // Copy the hasDonnees and table data fields
                        indicateurDto.setHasDonnees(indicateurWithTableData.isHasDonnees());
                        indicateurDto.setPivotTableData(indicateurWithTableData.getPivotTableData());
                        indicateurDto.setFlatTableData(indicateurWithTableData.getFlatTableData());
                        indicateurDto.setCrudTableData(indicateurWithTableData.getCrudTableData());
                        indicateurDto.setCreateTemplateData(indicateurWithTableData.getCreateTemplateData());
                    }
                }
            }
        }

        return dto;
    }

}