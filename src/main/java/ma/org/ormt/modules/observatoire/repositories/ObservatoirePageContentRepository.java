package ma.org.ormt.modules.observatoire.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;

public interface ObservatoirePageContentRepository extends JpaRepository<ObservatoirePageContent, Long> {

    Optional<ObservatoirePageContent> findFirstByOrderByLastModifiedDateDescIdDesc();

    Optional<ObservatoirePageContent> findFirstByActifTrueAndPublishedTrueOrderByLastModifiedDateDescIdDesc();
}