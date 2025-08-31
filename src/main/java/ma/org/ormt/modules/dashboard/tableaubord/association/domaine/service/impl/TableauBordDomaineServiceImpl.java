package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.service.impl;

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
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.ReorderTBDomaineItem;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.TableauBordDomaineRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.repository.TableauBordDomaineRepository;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.service.TableauBordDomaineService;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.repositories.TableauBordRepository;

@Service
public class TableauBordDomaineServiceImpl extends BaseServiceImpl<TableauBordDomaine>
        implements TableauBordDomaineService {

    @Autowired
    private TableauBordDomaineRepository tableauTableauBordDomaineRepository;

    @Autowired
    private TBDomaineService tbDomaineService;

    @Autowired
    private TableauBordRepository tableauBordRepository;

    @Autowired
    private ObjectsValidator<TableauBordDomaineRequestDto> validator;

    static final String TBDOMAIN_NOT_FOUND = "TBDomaine not found";
    static final String TABLEAUTableauBord_NOT_FOUND = "TableauBord not found";
    static final String NOT_FOUND = "attach not found";

    public TableauBordDomaineServiceImpl(TableauBordDomaineRepository tableauBordDomaineRepository,
            SpecificationService specificationService) {
        super(tableauBordDomaineRepository, specificationService);
    }

    @Transactional
    public List<TableauBordDomaine> attachDomainesToTableauBord(
            List<TableauBordDomaineRequestDto> tableauTableauBordDomaineRequestDtos) {

        List<TableauBordDomaine> tableauTableauBordDomTableauBordDomaines = tableauTableauBordDomaineRequestDtos
                .stream().map(requestDto -> {

                    validator.validate(requestDto);

                    TableauBord tableauTableauBord = tableauBordRepository.findById(requestDto.getTableauBord().getId())
                            .orElseThrow(() -> new EntityNotFoundException(TABLEAUTableauBord_NOT_FOUND));

                    TBDomaine tbDomaine = tbDomaineService.findById(requestDto.getTbDomaine().getId())
                            .orElseThrow(() -> new EntityNotFoundException(TBDOMAIN_NOT_FOUND));

                    TableauBordDomaine tableauBordDomaine = new TableauBordDomaine();
                    tableauBordDomaine.setTbDomaine(tbDomaine);
                    tableauBordDomaine.setTableauBord(tableauTableauBord);
                    tableauBordDomaine.setOrdre(requestDto.getOrdre());
                    return tableauTableauBordDomaineRepository.save(tableauBordDomaine);
                }).toList();

        return tableauTableauBordDomTableauBordDomaines;
    }

    @Transactional
    public void detachDomainesFromTableauBord(List<Long> tableauTableauBordDomaineIds) {

        tableauTableauBordDomaineRepository.deleteAllById(tableauTableauBordDomaineIds);

    }

    @Transactional
    public void reorderDomaines(Long tableauTableauBordId,
            List<ReorderTBDomaineItem> items) {
        // Validate tableauTableauBord exists
        tableauBordRepository.findById(tableauTableauBordId)
                .orElseThrow(() -> new EntityNotFoundException(TABLEAUTableauBord_NOT_FOUND));

        // Load existing associations for tableauTableauBord
        List<TableauBordDomaine> existing = tableauTableauBordDomaineRepository
                .findByTableauBordIdOrderByOrdreAsc(tableauTableauBordId);
        if (existing.isEmpty()) {
            return; // nothing to reorder
        }

        Map<Long, TableauBordDomaine> byId = existing.stream()
                .collect(Collectors.toMap(TableauBordDomaine::getId, ed -> ed));

        Set<Long> currentIds = byId.keySet();
        Set<Long> requestedIds = items.stream().map(i -> i.getTableauBordDomaineId()).collect(Collectors.toSet());

        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException(
                    "Reorder items must include all and only current associations for this tableauTableauBord");
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
                        ReorderTBDomaineItem::getTableauBordDomaineId,
                        ReorderTBDomaineItem::getOrdre));

        boolean anyChanged = false;
        for (TableauBordDomaine ed : existing) {
            Integer newOrdre = newOrdreById.get(ed.getId());
            if (newOrdre != null && !newOrdre.equals(ed.getOrdre())) {
                ed.setOrdre(newOrdre);
                anyChanged = true;
            }
        }

        if (anyChanged) {
            tableauTableauBordDomaineRepository.saveAll(existing);
        }
    }

}