package ma.org.ormt.modules.observatoire.services;

import java.util.Optional;

import ma.org.ormt.modules.observatoire.dtos.request.ObservatoirePageContentRequestDto;
import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;

public interface ObservatoirePageContentService {

    Optional<ObservatoirePageContent> findCurrent();

    Optional<ObservatoirePageContent> findPublished();

    ObservatoirePageContent create(ObservatoirePageContentRequestDto requestDto);

    ObservatoirePageContent update(Long id, ObservatoirePageContentRequestDto requestDto);
}