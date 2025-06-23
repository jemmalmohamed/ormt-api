package ma.org.ormt.modules.publications.publication.services.impl;

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
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.publications.publication.dtos.request.PublicationRequestDto;
import ma.org.ormt.modules.publications.publication.dtos.request.PublicationRequestDtoMapper;
import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.modules.publications.publication.repositories.PublicationRepository;
import ma.org.ormt.modules.publications.publication.services.PublicationService;

@Service
public class PublicationServiceImpl extends BaseServiceImpl<Publication> implements PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectsValidator<PublicationRequestDto> validator;

    @Autowired
    private PublicationRequestDtoMapper publicationRequestMapper;

    static final String NOT_FOUND_STRING = "Publication not found";

    public PublicationServiceImpl(PublicationRepository publicationRepository,
            SpecificationService specificationService) {
        super(publicationRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return publicationRepository.existsById(id);
    }

    @Override
    public Optional<Publication> findByTitre(String titre) {
        return publicationRepository.findByTitre(titre);
    }

    @Override
    public Page<Publication> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Publication.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Publication> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Publication.class);
        return findAll(specification, pageable);
    }

    @Override
    public Publication create(PublicationRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        MultipartFile optimizedFichier = requestDto.getFichier();

        String fichierFileName = minioService.uploadFile(optimizedFichier);
        Publication publicationToCreate = publicationRequestMapper.mapToEntity(requestDto);
        publicationToCreate.setFichierUrl(fichierFileName); // Store just the filename

        return publicationRepository.save(publicationToCreate);
    }

    @Override
    public Publication update(Long id, PublicationRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updatePublicationFields(publication, requestDto);
        handleFichierUpdate(publication, requestDto);
        return publicationRepository.save(publication);
    }

    private void updatePublicationFields(Publication publication, PublicationRequestDto requestDto) {
        publication.setTitre(requestDto.getTitre());
        publication.setDescription(requestDto.getDescription());
        publication.setAuteur(requestDto.getAuteur());
        publication.setDatePublication(requestDto.getDatePublication());
        publication.setCategorie(requestDto.getCategorie());
        publication.setTags(requestDto.getTags());
        publication.setNombreTelechargements(requestDto.getNombreTelechargements());
        // fichierUrl, nomFichier, tailleFichier are handled in handleFichierUpdate
    }

    private void handleFichierUpdate(Publication publication, PublicationRequestDto dto) throws Exception {
        if (dto.getFichier() != null && !dto.getFichier().isEmpty()) {

            String fichierFileName = minioService.uploadFile(dto.getFichier());
            publication.setFichierUrl(fichierFileName);

        }
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validatePublicationDependencies(id);
    }

    private void validatePublicationDependencies(Long id) {

    }

}