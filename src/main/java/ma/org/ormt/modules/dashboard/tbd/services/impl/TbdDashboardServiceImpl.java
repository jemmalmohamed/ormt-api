package ma.org.ormt.modules.dashboard.tbd.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.dashboard.tableaubord.v2.repositories.TableauBordV2CategorieRepository;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdAssignationDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardSummaryDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdSectionDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdSourceDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdWidgetDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdWidgetRowDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardAssignRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardUpdateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdRowResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdSectionCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdSectionResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetRowCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetRowHeightUpdateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetUpdateContentRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetUpdateIndicatorRequest;
import ma.org.ormt.modules.dashboard.tbd.models.TbdAssignation;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSourceListing;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdAssignationRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSectionRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSourceListingRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRowRepository;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;

@Service
@RequiredArgsConstructor
public class TbdDashboardServiceImpl implements TbdDashboardService {

    private static final String DASHBOARD_NOT_FOUND = "Dashboard not found";
    private static final String DASHBOARD_TITLE_ALREADY_EXISTS = "Un dashboard avec ce titre existe deja";
    private static final String CATEGORY_ALREADY_ASSIGNED = "Cette categorie est deja assignee a un autre dashboard";
    private static final String SECTION_NOT_FOUND = "Section not found";
    private static final String ROW_NOT_FOUND = "Row not found";
    private static final String WIDGET_NOT_FOUND = "Widget not found";

    private final TbdDashboardRepository dashboardRepository;
    private final TbdAssignationRepository assignationRepository;
    private final TbdSourceListingRepository sourceListingRepository;
    private final TbdSectionRepository sectionRepository;
    private final TbdWidgetRowRepository widgetRowRepository;
    private final TbdWidgetRepository widgetRepository;
    private final DomaineRepository domaineRepository;
    private final TableauBordV2CategorieRepository categorieRepository;
    private final IndicateurRepository indicateurRepository;

    @Override
    public TbdDashboardFullDto findById(Long id) {
        TbdDashboard dashboard = dashboardRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));

        Optional<TbdAssignation> assignation = assignationRepository.findByDashboardId(id);

        List<TbdSourceDto> sources = sourceListingRepository.findByDashboardIdOrderByOrdreAsc(id).stream()
                .map(sl -> TbdSourceDto.builder()
                        .id(sl.getSource().getId())
                        .nom(sl.getSource().getNom())
                        .abreviation(sl.getSource().getAbreviation())
                        .url(sl.getSource().getUrl())
                        .build())
                .collect(Collectors.toList());

        List<TbdSection> sections = sectionRepository.findByDashboardIdOrderByOrdreAsc(id);
        List<Long> sectionIds = sections.stream().map(TbdSection::getId).collect(Collectors.toList());

        List<TbdWidgetRow> allRows = sectionIds.isEmpty()
                ? new ArrayList<>()
                : widgetRowRepository.findBySectionIdInOrderBySectionIdAscOrdreAsc(sectionIds);
        List<Long> rowIds = allRows.stream().map(TbdWidgetRow::getId).collect(Collectors.toList());

        List<TbdWidget> allWidgets = rowIds.isEmpty()
                ? new ArrayList<>()
                : widgetRepository.findByRowIdInOrderByRowIdAscOrdreAsc(rowIds);

        Map<Long, List<TbdWidget>> widgetsByRowId = allWidgets.stream()
                .collect(Collectors.groupingBy(TbdWidget::getRowId));

        Map<Long, List<TbdWidgetRow>> rowsBySectionId = allRows.stream()
                .collect(Collectors.groupingBy(TbdWidgetRow::getSectionId));

        List<TbdSectionDto> sectionDtos = sections.stream().map(section -> {
            List<TbdWidgetRow> rows = rowsBySectionId.getOrDefault(section.getId(), new ArrayList<>());
            List<TbdWidgetRowDto> rowDtos = rows.stream().map(row -> {
                List<TbdWidget> widgets = widgetsByRowId.getOrDefault(row.getId(), new ArrayList<>());
                List<TbdWidgetDto> widgetDtos = widgets.stream().map(this::toWidgetDto).collect(Collectors.toList());
                return TbdWidgetRowDto.builder()
                        .id(row.getId())
                        .ordre(row.getOrdre())
                        .sizePercent(row.getSizePercent())
                        .heightPx(row.getHeightPx())
                        .widgets(widgetDtos)
                        .build();
            }).collect(Collectors.toList());

            return TbdSectionDto.builder()
                    .id(section.getId())
                    .label(section.getLabel())
                    .ordre(section.getOrdre())
                    .sizePercent(section.getSizePercent())
                    .rows(rowDtos)
                    .build();
        }).collect(Collectors.toList());

        TbdAssignationDto assignationDto = assignation.map(a -> {
            String cibleNom = resolveCibleNom(a.getCibleType(), a.getCibleId());
            return TbdAssignationDto.builder()
                    .cibleType(a.getCibleType())
                    .cibleId(a.getCibleId())
                    .cibleNom(cibleNom)
                    .build();
        }).orElse(null);

        return TbdDashboardFullDto.builder()
                .id(dashboard.getId())
                .nom(dashboard.getNom())
                .titre(dashboard.getTitre())
                .sousTitre(dashboard.getSousTitre())
                .description(dashboard.getDescription())
                .sourceText(dashboard.getSourceText())
                .periodeLabel(dashboard.getPeriodeLabel())
                .actif(dashboard.getActif())
                .status(dashboard.getStatus())
                .assignation(assignationDto)
                .sources(sources)
                .sections(sectionDtos)
                .build();
    }

    @Override
    public Page<TbdDashboardSummaryDto> findAll(Pageable pageable) {
        Page<TbdDashboard> page = dashboardRepository.findByActifTrueOrderByLastModifiedDateDesc(pageable);
        List<TbdDashboardSummaryDto> dtos = page.getContent().stream().map(d -> {
            Optional<TbdAssignation> assignation = assignationRepository.findByDashboardId(d.getId());
            String assignationNom = assignation.map(a -> resolveCibleNom(a.getCibleType(), a.getCibleId())).orElse(null);
            int nbSections = sectionRepository.findByDashboardIdAndActifTrue(d.getId()).size();
            return TbdDashboardSummaryDto.builder()
                    .id(d.getId())
                    .nom(d.getNom())
                    .titre(d.getTitre())
                    .sousTitre(d.getSousTitre())
                    .status(d.getStatus())
                    .actif(d.getActif())
                    .assignationNom(assignationNom)
                    .nbSections(nbSections)
                    .lastModifiedDate(d.getLastModifiedDate())
                    .build();
        }).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public TbdDashboard create(TbdDashboardCreateRequest request) {
        validateUniqueTitre(request.getTitre(), null);
        TbdDashboard dashboard = TbdDashboard.builder()
                .nom(request.getNom())
                .titre(request.getTitre())
                .sousTitre(request.getSousTitre())
                .description(request.getDescription())
                .sourceText(request.getSourceText())
                .periodeLabel(request.getPeriodeLabel())
                .actif(true)
                .status("DRAFT")
                .build();
        return dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public TbdDashboard update(Long id, TbdDashboardUpdateRequest request) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        validateUniqueTitre(request.getTitre(), id);
        dashboard.setNom(request.getNom());
        dashboard.setTitre(request.getTitre());
        dashboard.setSousTitre(request.getSousTitre());
        dashboard.setDescription(request.getDescription());
        dashboard.setSourceText(request.getSourceText());
        dashboard.setPeriodeLabel(request.getPeriodeLabel());
        return dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public TbdDashboard duplicate(Long id) {
        TbdDashboard sourceDashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));

        String duplicatedNom = nextDuplicatedLabel(sourceDashboard.getNom());
        String duplicatedTitre = sourceDashboard.getTitre() == null || sourceDashboard.getTitre().isBlank()
                ? null
                : nextDuplicatedTitre(sourceDashboard.getTitre());

        TbdDashboard duplicatedDashboard = dashboardRepository.save(TbdDashboard.builder()
                .nom(duplicatedNom)
                .titre(duplicatedTitre)
                .sousTitre(sourceDashboard.getSousTitre())
                .description(sourceDashboard.getDescription())
                .sourceText(sourceDashboard.getSourceText())
                .periodeLabel(sourceDashboard.getPeriodeLabel())
                .actif(true)
                .status("DRAFT")
                .build());

        duplicateSources(id, duplicatedDashboard.getId());
        duplicateSections(id, duplicatedDashboard.getId());
        return duplicatedDashboard;
    }

    @Override
    public List<Long> findAssignedCategoryIds(Long excludeDashboardId) {
        return assignationRepository.findByCibleType("CATEGORIE").stream()
                .filter(assignation -> excludeDashboardId == null || !assignation.getDashboardId().equals(excludeDashboardId))
                .map(TbdAssignation::getCibleId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        assignationRepository.deleteByDashboardId(id);
        dashboard.setActif(false);
        dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        dashboard.setStatus("PUBLISHED");
        dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public void setDraft(Long id) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        dashboard.setStatus("DRAFT");
        dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public void archive(Long id) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        dashboard.setStatus("ARCHIVED");
        dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public TbdAssignation assign(Long dashboardId, TbdDashboardAssignRequest request) {
        if ("CATEGORIE".equals(request.getCibleType())) {
            assignationRepository.findByCibleTypeAndCibleId(request.getCibleType(), request.getCibleId())
                    .ifPresent(existing -> {
                        if (!existing.getDashboardId().equals(dashboardId)) {
                            throw new IllegalArgumentException(CATEGORY_ALREADY_ASSIGNED);
                        }
                    });
        }
        assignationRepository.deleteByDashboardId(dashboardId);
        TbdAssignation assignation = TbdAssignation.builder()
                .dashboardId(dashboardId)
                .cibleType(request.getCibleType())
                .cibleId(request.getCibleId())
                .build();
        return assignationRepository.save(assignation);
    }

    @Override
    @Transactional
    public void removeAssignation(Long dashboardId) {
        assignationRepository.deleteByDashboardId(dashboardId);
    }

    @Override
    @Transactional
    public TbdSection addSection(Long dashboardId, TbdSectionCreateRequest request) {
        TbdSection section = TbdSection.builder()
                .dashboardId(dashboardId)
                .label(request.getLabel())
                .ordre(request.getOrdre() != null ? request.getOrdre() : 0)
                .sizePercent(request.getSizePercent() != null ? request.getSizePercent() : 33)
                .actif(true)
                .build();
        TbdSection saved = sectionRepository.save(section);
        redistributeSections(dashboardId);
        return saved;
    }

    @Override
    @Transactional
    public void resizeSections(Long dashboardId, TbdSectionResizeRequest request) {
        int sum = request.getItems().stream().mapToInt(i -> i.getSizePercent()).sum();
        if (sum != 100) {
            throw new IllegalArgumentException("La somme des tailles doit être égale à 100. Valeur reçue : " + sum);
        }
        request.getItems().forEach(item -> {
            TbdSection section = sectionRepository.findById(item.getSectionId())
                    .orElseThrow(() -> new EntityNotFoundException(SECTION_NOT_FOUND));
            section.setSizePercent(item.getSizePercent());
            sectionRepository.save(section);
        });
    }

    @Override
    @Transactional
    public void reorderSections(Long dashboardId, List<Long> orderedSectionIds) {
        for (int i = 0; i < orderedSectionIds.size(); i++) {
            TbdSection section = sectionRepository.findById(orderedSectionIds.get(i))
                    .orElseThrow(() -> new EntityNotFoundException(SECTION_NOT_FOUND));
            section.setOrdre(i + 1);
            sectionRepository.save(section);
        }
    }

    @Override
    @Transactional
    public void deleteSection(Long sectionId) {
        TbdSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException(SECTION_NOT_FOUND));
        Long dashboardId = section.getDashboardId();
        sectionRepository.deleteById(sectionId);
        redistributeSections(dashboardId);
    }

    @Override
    @Transactional
    public TbdWidgetRow addRow(Long sectionId, TbdWidgetRowCreateRequest request) {
        TbdWidgetRow row = TbdWidgetRow.builder()
                .sectionId(sectionId)
                .ordre(request.getOrdre() != null ? request.getOrdre() : 0)
                .sizePercent(request.getSizePercent() != null ? request.getSizePercent() : 50)
                .heightPx(request.getHeightPx() != null ? request.getHeightPx() : 200)
                .build();
        TbdWidgetRow saved = widgetRowRepository.save(row);
        redistributeRows(sectionId);
        return saved;
    }

    @Override
    @Transactional
    public void resizeRows(Long sectionId, TbdRowResizeRequest request) {
        int sum = request.getItems().stream().mapToInt(i -> i.getSizePercent()).sum();
        if (sum != 100) {
            throw new IllegalArgumentException("La somme des tailles doit être égale à 100. Valeur reçue : " + sum);
        }
        request.getItems().forEach(item -> {
            TbdWidgetRow row = widgetRowRepository.findById(item.getRowId())
                    .orElseThrow(() -> new EntityNotFoundException(ROW_NOT_FOUND));
            row.setSizePercent(item.getSizePercent());
            widgetRowRepository.save(row);
        });
    }

    @Override
    @Transactional
    public void reorderRows(Long sectionId, List<Long> orderedRowIds) {
        for (int i = 0; i < orderedRowIds.size(); i++) {
            TbdWidgetRow row = widgetRowRepository.findById(orderedRowIds.get(i))
                    .orElseThrow(() -> new EntityNotFoundException(ROW_NOT_FOUND));
            row.setOrdre(i + 1);
            widgetRowRepository.save(row);
        }
    }

    @Override
    @Transactional
    public void updateRowHeight(Long rowId, TbdWidgetRowHeightUpdateRequest request) {
        TbdWidgetRow row = widgetRowRepository.findById(rowId)
                .orElseThrow(() -> new EntityNotFoundException(ROW_NOT_FOUND));
        row.setHeightPx(Math.max(80, request.getHeightPx()));
        widgetRowRepository.save(row);
    }

    @Override
    @Transactional
    public void deleteRow(Long rowId) {
        TbdWidgetRow row = widgetRowRepository.findById(rowId)
                .orElseThrow(() -> new EntityNotFoundException(ROW_NOT_FOUND));
        Long sectionId = row.getSectionId();
        widgetRowRepository.deleteById(rowId);
        redistributeRows(sectionId);
    }

    @Override
    @Transactional
    public TbdWidget addWidget(TbdWidgetCreateRequest request) {
        TbdWidget widget = TbdWidget.builder()
                .rowId(request.getRowId())
                .type(request.getType())
                .kpiId(request.getKpiId())
                .contentJson(request.getContentJson())
                .titre(request.getTitre())
                .ordre(request.getOrdre() != null ? request.getOrdre() : 0)
                .sizePercent(request.getSizePercent() != null ? request.getSizePercent() : 50)
                .actif(true)
                .build();
        if (request.getIndicateurId() != null) {
            indicateurRepository.findById(request.getIndicateurId()).ifPresent(widget::setIndicateur);
        }
        TbdWidget saved = widgetRepository.save(widget);
        redistributeWidgets(request.getRowId());
        return saved;
    }

    @Override
    @Transactional
    public void resizeWidgets(Long rowId, TbdWidgetResizeRequest request) {
        int sum = request.getItems().stream().mapToInt(i -> i.getSizePercent()).sum();
        if (sum != 100) {
            throw new IllegalArgumentException("La somme des tailles doit être égale à 100. Valeur reçue : " + sum);
        }
        request.getItems().forEach(item -> {
            TbdWidget widget = widgetRepository.findById(item.getWidgetId())
                    .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
            widget.setSizePercent(item.getSizePercent());
            widgetRepository.save(widget);
        });
    }

    @Override
    @Transactional
    public void reorderWidgets(Long rowId, List<Long> orderedWidgetIds) {
        for (int i = 0; i < orderedWidgetIds.size(); i++) {
            TbdWidget widget = widgetRepository.findById(orderedWidgetIds.get(i))
                    .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
            widget.setOrdre(i + 1);
            widgetRepository.save(widget);
        }
    }

    @Override
    @Transactional
    public void deleteWidget(Long widgetId) {
        TbdWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
        Long rowId = widget.getRowId();
        widgetRepository.deleteById(widgetId);
        redistributeWidgets(rowId);
    }

    @Override
    @Transactional
    public void updateWidgetContent(Long widgetId, TbdWidgetUpdateContentRequest request) {
        TbdWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
        widget.setContentJson(request.getContentJson());
        widgetRepository.save(widget);
    }

    @Override
    @Transactional
    public void updateWidgetIndicator(Long widgetId, TbdWidgetUpdateIndicatorRequest request) {
        TbdWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
        if (request.getIndicateurId() == null) {
            widget.setIndicateur(null);
        } else {
            widget.setIndicateur(indicateurRepository.findById(request.getIndicateurId())
                    .orElseThrow(() -> new EntityNotFoundException("Indicateur introuvable.")));
        }
        widgetRepository.save(widget);
    }

    // --- private helpers ---

    private void redistributeSections(Long dashboardId) {
        List<TbdSection> sections = sectionRepository.findByDashboardIdOrderByOrdreAsc(dashboardId);
        if (sections.isEmpty()) return;
        redistributePercent(
                sections.stream().map(TbdSection::getId).collect(Collectors.toList()),
                (sectionId, pct) -> {
                    TbdSection s = sections.stream().filter(sec -> sec.getId().equals(sectionId)).findFirst().get();
                    s.setSizePercent(pct);
                    sectionRepository.save(s);
                });
    }

    private void redistributeRows(Long sectionId) {
        List<TbdWidgetRow> rows = widgetRowRepository.findBySectionIdOrderByOrdreAsc(sectionId);
        if (rows.isEmpty()) return;
        redistributePercent(
                rows.stream().map(TbdWidgetRow::getId).collect(Collectors.toList()),
                (rowId, pct) -> {
                    TbdWidgetRow r = rows.stream().filter(row -> row.getId().equals(rowId)).findFirst().get();
                    r.setSizePercent(pct);
                    widgetRowRepository.save(r);
                });
    }

    private void redistributeWidgets(Long rowId) {
        List<TbdWidget> widgets = widgetRepository.findByRowIdOrderByOrdreAsc(rowId);
        if (widgets.isEmpty()) return;
        redistributePercent(
                widgets.stream().map(TbdWidget::getId).collect(Collectors.toList()),
                (widgetId, pct) -> {
                    TbdWidget w = widgets.stream().filter(wgt -> wgt.getId().equals(widgetId)).findFirst().get();
                    w.setSizePercent(pct);
                    widgetRepository.save(w);
                });
    }

    private void redistributePercent(List<Long> ids, BiConsumer<Long, Integer> updater) {
        int size = ids.size();
        int base = 100 / size;
        int remainder = 100 % size;
        for (int i = 0; i < size; i++) {
            int pct = base + (i == size - 1 ? remainder : 0);
            updater.accept(ids.get(i), pct);
        }
    }

    private void duplicateSources(Long sourceDashboardId, Long targetDashboardId) {
        List<TbdSourceListing> sourceListings = sourceListingRepository.findByDashboardIdOrderByOrdreAsc(sourceDashboardId);
        for (TbdSourceListing sourceListing : sourceListings) {
            sourceListingRepository.save(TbdSourceListing.builder()
                    .dashboardId(targetDashboardId)
                    .source(sourceListing.getSource())
                    .ordre(sourceListing.getOrdre())
                    .build());
        }
    }

    private void duplicateSections(Long sourceDashboardId, Long targetDashboardId) {
        List<TbdSection> sourceSections = sectionRepository.findByDashboardIdOrderByOrdreAsc(sourceDashboardId);
        for (TbdSection sourceSection : sourceSections) {
            TbdSection duplicatedSection = sectionRepository.save(TbdSection.builder()
                    .dashboardId(targetDashboardId)
                    .label(sourceSection.getLabel())
                    .ordre(sourceSection.getOrdre())
                    .sizePercent(sourceSection.getSizePercent())
                    .actif(sourceSection.getActif())
                    .build());
            duplicateRows(sourceSection.getId(), duplicatedSection.getId());
        }
    }

    private void duplicateRows(Long sourceSectionId, Long targetSectionId) {
        List<TbdWidgetRow> sourceRows = widgetRowRepository.findBySectionIdOrderByOrdreAsc(sourceSectionId);
        for (TbdWidgetRow sourceRow : sourceRows) {
            TbdWidgetRow duplicatedRow = widgetRowRepository.save(TbdWidgetRow.builder()
                    .sectionId(targetSectionId)
                    .ordre(sourceRow.getOrdre())
                    .sizePercent(sourceRow.getSizePercent())
                    .heightPx(sourceRow.getHeightPx())
                    .build());
            duplicateWidgets(sourceRow.getId(), duplicatedRow.getId());
        }
    }

    private void duplicateWidgets(Long sourceRowId, Long targetRowId) {
        List<TbdWidget> sourceWidgets = widgetRepository.findByRowIdOrderByOrdreAsc(sourceRowId);
        for (TbdWidget sourceWidget : sourceWidgets) {
            widgetRepository.save(TbdWidget.builder()
                    .rowId(targetRowId)
                    .type(sourceWidget.getType())
                    .indicateur(sourceWidget.getIndicateur())
                    .kpiId(sourceWidget.getKpiId())
                    .contentJson(sourceWidget.getContentJson())
                    .titre(sourceWidget.getTitre())
                    .ordre(sourceWidget.getOrdre())
                    .sizePercent(sourceWidget.getSizePercent())
                    .actif(sourceWidget.getActif())
                    .build());
        }
    }

    private String nextDuplicatedLabel(String sourceLabel) {
        String baseLabel = (sourceLabel == null || sourceLabel.isBlank()) ? "dashboard" : sourceLabel.trim();
        String candidate = baseLabel + " copie";
        int suffix = 1;
        while (dashboardRepository.findByNomIgnoreCaseAndActifTrue(candidate).isPresent()) {
            candidate = baseLabel + " copie " + suffix;
            suffix++;
        }
        return candidate;
    }

    private String nextDuplicatedTitre(String sourceTitre) {
        String baseTitre = sourceTitre.trim();
        String candidate = baseTitre + " copie";
        int suffix = 1;
        while (dashboardRepository.findByTitreIgnoreCaseAndActifTrue(candidate).isPresent()) {
            candidate = baseTitre + " copie " + suffix;
            suffix++;
        }
        return candidate;
    }

    private void validateUniqueTitre(String titre, Long currentDashboardId) {
        if (titre == null || titre.isBlank()) {
            return;
        }

        dashboardRepository.findByTitreIgnoreCaseAndActifTrue(titre.trim()).ifPresent(existing -> {
            if (currentDashboardId == null || !existing.getId().equals(currentDashboardId)) {
                throw new IllegalArgumentException(DASHBOARD_TITLE_ALREADY_EXISTS);
            }
        });
    }

    private String resolveCibleNom(String cibleType, Long cibleId) {
        if ("DOMAINE".equals(cibleType)) {
            return domaineRepository.findById(cibleId).map(d -> d.getNom()).orElse(null);
        } else if ("CATEGORIE".equals(cibleType)) {
            return categorieRepository.findById(cibleId)
                    .map(c -> {
                        String domaineNom = c.getTbDomaine() != null
                                ? (c.getTbDomaine().getLibelle() != null && !c.getTbDomaine().getLibelle().isBlank()
                                        ? c.getTbDomaine().getLibelle()
                                        : c.getTbDomaine().getNom())
                                : null;
                        String categorieNom = c.getLibelle();
                        if (domaineNom == null || domaineNom.isBlank()) {
                            return categorieNom;
                        }
                        return domaineNom + " - " + categorieNom;
                    })
                    .orElse(null);
        }
        return null;
    }

    private TbdWidgetDto toWidgetDto(TbdWidget widget) {
        String indicateurNom = null;
        String indicateurTitre = null;
        Long indicateurId = null;
        if (widget.getIndicateur() != null) {
            indicateurId = widget.getIndicateur().getId();
            indicateurNom = widget.getIndicateur().getNom();
            indicateurTitre = widget.getIndicateur().getTitre();
        }
        return TbdWidgetDto.builder()
                .id(widget.getId())
                .type(widget.getType())
                .titre(widget.getTitre())
                .ordre(widget.getOrdre())
                .sizePercent(widget.getSizePercent())
                .indicateurId(indicateurId)
                .indicateurNom(indicateurNom)
                .indicateurTitre(indicateurTitre)
                .kpiId(widget.getKpiId())
                .contentJson(widget.getContentJson())
                .build();
    }
}
