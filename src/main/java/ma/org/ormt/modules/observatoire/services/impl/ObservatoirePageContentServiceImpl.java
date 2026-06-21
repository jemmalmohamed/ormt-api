package ma.org.ormt.modules.observatoire.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePageContentMapper;
import ma.org.ormt.modules.observatoire.dtos.request.ObservatoirePageContentRequestDto;
import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;
import ma.org.ormt.modules.observatoire.repositories.ObservatoirePageContentRepository;
import ma.org.ormt.modules.observatoire.services.ObservatoirePageContentService;

@Service
@Transactional
@RequiredArgsConstructor
public class ObservatoirePageContentServiceImpl implements ObservatoirePageContentService {

    private static final String NOT_FOUND_MESSAGE = "Contenu Observatoire non trouve";

    private final ObservatoirePageContentRepository repository;
    private final ObservatoirePageContentMapper mapper;
    private final ObjectsValidator<ObservatoirePageContentRequestDto> validator;

    @Override
    @Transactional(readOnly = true)
    public Optional<ObservatoirePageContent> findCurrent() {
        return repository.findFirstByOrderByLastModifiedDateDescIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ObservatoirePageContent> findPublished() {
        return repository.findFirstByActifTrueAndPublishedTrueOrderByLastModifiedDateDescIdDesc();
    }

    @Override
    public ObservatoirePageContent create(ObservatoirePageContentRequestDto requestDto) {
        validator.validate(requestDto);
        ObservatoirePageContent entity = mapper.mapToEntity(requestDto);
        return repository.save(entity);
    }

    @Override
    public ObservatoirePageContent update(Long id, ObservatoirePageContentRequestDto requestDto) {
        validator.validate(requestDto);
        if (requestDto.getId() != null && !id.equals(requestDto.getId())) {
            throw new IllegalArgumentException(
                    "L'identifiant dans le chemin et dans le corps de la requete ne sont pas les memes");
        }

        ObservatoirePageContent entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
        mapper.updateEntity(entity, requestDto);
        return repository.save(entity);
    }
}