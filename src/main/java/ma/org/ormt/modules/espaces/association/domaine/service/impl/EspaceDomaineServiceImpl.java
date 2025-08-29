package ma.org.ormt.modules.espaces.association.domaine.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.dtos.request.EspaceDomaineRequestDto;
import ma.org.ormt.modules.espaces.association.domaine.repository.EspaceDomaineRepository;
import ma.org.ormt.modules.espaces.association.domaine.service.EspaceDomaineService;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;

@Service
public class EspaceDomaineServiceImpl extends BaseServiceImpl<Espace> implements EspaceDomaineService {

    @Autowired
    private EspaceDomaineRepository espaceDomaineRepository;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<EspaceDomaineRequestDto> validator;

    static final String DOMAINE_NOT_FOUND = "Domaine not found";
    static final String ESPACE_NOT_FOUND = "Espace not found";
    static final String NOT_FOUND = "attach not found";

    public EspaceDomaineServiceImpl(EspaceRepository espaceRepository, SpecificationService specificationService) {
        super(espaceRepository, specificationService);
    }

    @Transactional
    public List<EspaceDomaine> attachDomainesToEspace(List<EspaceDomaineRequestDto> espaceDomaineRequestDtos) {

        List<EspaceDomaine> espaceDomEspaceDomaines = espaceDomaineRequestDtos.stream().map(requestDto -> {

            validator.validate(requestDto);

            Espace espace = findById(requestDto.getEspace().getId())
                    .orElseThrow(() -> new EntityNotFoundException(ESPACE_NOT_FOUND));

            Domaine domaine = domaineService.findById(requestDto.getDomaine().getId())
                    .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

            EspaceDomaine espaceDomEspaceDomaine = new EspaceDomaine();
            espaceDomEspaceDomaine.setDomaine(domaine);
            espaceDomEspaceDomaine.setEspace(espace);
            espaceDomEspaceDomaine.setOrdre(requestDto.getOrdre());
            return espaceDomaineRepository.save(espaceDomEspaceDomaine);
        }).toList();

        return espaceDomEspaceDomaines;
    }

    @Transactional
    public void detachDomainesFromEspace(List<Long> espaceDomaineIds) {

        espaceDomaineRepository.deleteAllById(espaceDomaineIds);

    }

    @Transactional
    public void reorderDomaines(Long espaceId,
            List<ma.org.ormt.modules.espaces.association.domaine.dtos.request.ReorderDomaineItem> items) {
        // Validate espace exists
        findById(espaceId).orElseThrow(() -> new EntityNotFoundException(ESPACE_NOT_FOUND));

        // Load existing associations for espace
        List<EspaceDomaine> existing = espaceDomaineRepository.findByEspaceIdOrderByOrdreAsc(espaceId);
        if (existing.isEmpty()) {
            return; // nothing to reorder
        }

        Map<Long, EspaceDomaine> byId = existing.stream()
                .collect(Collectors.toMap(EspaceDomaine::getId, ed -> ed));

        Set<Long> currentIds = byId.keySet();
        Set<Long> requestedIds = items.stream().map(i -> i.getEspaceDomaineId()).collect(Collectors.toSet());

        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException(
                    "Reorder items must include all and only current associations for this espace");
        }

        int n = items.size();
        boolean withinRange = items.stream()
                .allMatch(i -> i.getOrdre() != null && i.getOrdre() >= 0 && i.getOrdre() < n);
        if (!withinRange) {
            throw new IllegalArgumentException("Invalid ordre values; must be within 0.." + (n - 1));
        }
        java.util.HashSet<Integer> ordreSet = new java.util.HashSet<>();
        for (var i : items) {
            if (!ordreSet.add(i.getOrdre())) {
                throw new IllegalArgumentException("Duplicate ordre values are not allowed");
            }
        }

        Map<Long, Integer> newOrdreById = items.stream()
                .collect(Collectors.toMap(
                        ma.org.ormt.modules.espaces.association.domaine.dtos.request.ReorderDomaineItem::getEspaceDomaineId,
                        ma.org.ormt.modules.espaces.association.domaine.dtos.request.ReorderDomaineItem::getOrdre));

        boolean anyChanged = false;
        for (EspaceDomaine ed : existing) {
            Integer newOrdre = newOrdreById.get(ed.getId());
            if (newOrdre != null && !newOrdre.equals(ed.getOrdre())) {
                ed.setOrdre(newOrdre);
                anyChanged = true;
            }
        }

        if (anyChanged) {
            espaceDomaineRepository.saveAll(existing);
        }
    }

}