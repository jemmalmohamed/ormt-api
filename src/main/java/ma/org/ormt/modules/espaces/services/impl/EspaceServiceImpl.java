package ma.org.ormt.modules.espaces.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.minio.MinioService;
// removed unused imports after refactor
import ma.org.ormt.core.utilities.files.ImageUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;
import ma.org.ormt.modules.espaces.services.EspaceService;

@Service
@Transactional
public class EspaceServiceImpl extends BaseServiceImpl<Espace> implements EspaceService {

    @Autowired
    private EspaceRepository espaceRepository;

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
        return super.existsById(id);
    }

    @Override
    public Optional<Espace> findByNom(String nom) {
        return espaceRepository.findByNom(nom);
    }

    @Override
    public Page<Espace> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, Espace.class);
    }

    @Override
    public Page<Espace> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        return super.getEntitiesByIds(ids, requestParams, Espace.class);
    }

    @Override
    public Espace save(Espace espace) {
        return espaceRepository.save(espace);
    }

    @Override
    public Espace create(EspaceRequestDto requestDto) throws Exception {

        validator.validate(requestDto);

        Espace espaceToCreate = espaceRequestMapper.mapToEntity(requestDto);
        MultipartFile optimizedImage = requestDto.getImageFile();
        if (optimizedImage != null && !optimizedImage.isEmpty()) {
            optimizedImage = ImageUtils.optimizeImageWithConverter(
                    optimizedImage, 1024, 1024, 0.8);
        }

        String imageFileName = minioService.uploadFile(optimizedImage);
        espaceToCreate.setImageUrl(imageFileName); // Store just the filename
        return espaceRepository.save(espaceToCreate);

    }

    @Override
    public Espace update(Long id, EspaceRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        Espace espace = espaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateEspaceFields(espace, requestDto);
        handleImageUpdate(espace, requestDto);
        return espaceRepository.save(espace);
    }

    private void updateEspaceFields(Espace espace, EspaceRequestDto dto) {
        espace.setNom(dto.getNom());
        espace.setDescription(dto.getDescription());
        espace.setApropos(dto.getApropos());
        espace.setActif(dto.getActif());
    }

    private void handleImageUpdate(Espace espace, EspaceRequestDto dto) throws Exception {
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {

            MultipartFile optimizedImage = ImageUtils.optimizeImageWithConverter(
                    dto.getImageFile(), 1024, 1024, 0.8);
            String imageFileName = minioService.uploadFile(optimizedImage);
            espace.setImageUrl(imageFileName);

        }
    }

}