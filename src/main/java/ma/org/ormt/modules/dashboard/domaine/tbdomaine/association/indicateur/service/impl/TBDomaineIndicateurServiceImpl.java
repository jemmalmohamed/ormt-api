package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service.impl;

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
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.ReorderIndicateurItem;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.TBDomaineIndicateurRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.repository.TBDomaineIndicateurRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service.TBDomaineIndicateurService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.repositories.TBDomaineRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
public class TBDomaineIndicateurServiceImpl extends BaseServiceImpl<TBDomaine> implements TBDomaineIndicateurService {

    @Autowired
    private TBDomaineIndicateurRepository tbDomaineIndicateurRepository;

    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private ObjectsValidator<TBDomaineIndicateurRequestDto> validator;

    static final String INDICATEUR_NOT_FOUND = "Indicateur not found";
    static final String TBDOMAINE_NOT_FOUND = "TBDomaine not found";
    static final String NOT_FOUND = "attach not found";

    public TBDomaineIndicateurServiceImpl(TBDomaineRepository tbDomaineRepository,
            SpecificationService specificationService) {
        super(tbDomaineRepository, specificationService);
    }

    @Transactional
    public List<TBDomaineIndicateur> attachIndicateursToTBDomaine(
            List<TBDomaineIndicateurRequestDto> tbDomaineIndicateurRequestDtos) {

        List<TBDomaineIndicateur> tbDomaineDomTBDomaineIndicateurs = tbDomaineIndicateurRequestDtos.stream()
                .map(requestDto -> {

                    validator.validate(requestDto);

                    TBDomaine tbDomaine = findById(requestDto.getTbDomaine().getId())
                            .orElseThrow(() -> new EntityNotFoundException(TBDOMAINE_NOT_FOUND));

                    Indicateur indicateur = indicateurService.findById(requestDto.getIndicateur().getId())
                            .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND));

                    TBDomaineIndicateur tbDomaineIndicateur = new TBDomaineIndicateur();
                    tbDomaineIndicateur.setIndicateur(indicateur);
                    tbDomaineIndicateur.setTbDomaine(tbDomaine);
                    tbDomaineIndicateur.setCategorie(requestDto.getCategorie());
                    tbDomaineIndicateur.setOrdre(requestDto.getOrdre());
                    return tbDomaineIndicateurRepository.save(tbDomaineIndicateur);
                }).toList();

        return tbDomaineDomTBDomaineIndicateurs;
    }

    @Transactional
    public void detachIndicateursFromTBDomaine(List<Long> tbDomaineIndicateurIds) {

        tbDomaineIndicateurRepository.deleteAllById(tbDomaineIndicateurIds);

    }

    @Transactional
    public void reorderIndicateurs(Long tbDomaineId,
            List<ReorderIndicateurItem> items) {
        findById(tbDomaineId).orElseThrow(() -> new EntityNotFoundException(TBDOMAINE_NOT_FOUND));

        // Load existing associations for tbDomaine
        List<TBDomaineIndicateur> existing = tbDomaineIndicateurRepository
                .findByTBDomaineIdOrderByOrdreAsc(tbDomaineId);
        if (existing.isEmpty()) {
            return; // nothing to reorder
        }

        Map<Long, TBDomaineIndicateur> byId = existing.stream()
                .collect(Collectors.toMap(TBDomaineIndicateur::getId, ed -> ed));

        Set<Long> currentIds = byId.keySet();
        Set<Long> requestedIds = items.stream().map(i -> i.getTbDomaineIndicateurId()).collect(Collectors.toSet());

        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException(
                    "Reorder items must include all and only current associations for this tBdomaine");
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
                        ReorderIndicateurItem::getTbDomaineIndicateurId,
                        ReorderIndicateurItem::getOrdre));

        boolean anyChanged = false;
        for (TBDomaineIndicateur ed : existing) {
            Integer newOrdre = newOrdreById.get(ed.getId());
            if (newOrdre != null && !newOrdre.equals(ed.getOrdre())) {
                ed.setOrdre(newOrdre);
                anyChanged = true;
            }
        }

        if (anyChanged) {
            tbDomaineIndicateurRepository.saveAll(existing);
        }
    }

}