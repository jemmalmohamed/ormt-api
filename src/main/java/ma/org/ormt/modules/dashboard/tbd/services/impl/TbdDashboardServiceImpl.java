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
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
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
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetUpdateKpiRequest;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSourceListing;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSectionRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSourceListingRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRowRepository;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;
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
    private final TbdSourceListingRepository sourceListingRepository;
    private final TbdSectionRepository sectionRepository;
    private final TbdWidgetRowRepository widgetRowRepository;
    private final TbdWidgetRepository widgetRepository;
    private final CategorieAnalytiqueRepository categorieRepository;
    private final IndicateurRepository indicateurRepository;
    private final ChiffreCleRepository chiffreCleRepository;
    private final ChiffreCleDtoMapper chiffreCleDtoMapper;

    @Override
    public TbdDashboardFullDto findById(Long id) {
        TbdDashboard dashboard = dashboardRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));

        Optional<CategorieAnalytique> assignation = categorieRepository.findByTbdDashboardId(id);

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
        Map<Long, ChiffreCle> kpisById = chiffreCleRepository.findAllById(
                allWidgets.stream()
                        .map(TbdWidget::getKpiId)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(ChiffreCle::getId, kpi -> kpi));

        Map<Long, List<TbdWidget>> widgetsByRowId = allWidgets.stream()
                .collect(Collectors.groupingBy(TbdWidget::getRowId));

        Map<Long, List<TbdWidgetRow>> rowsBySectionId = allRows.stream()
                .collect(Collectors.groupingBy(TbdWidgetRow::getSectionId));

        List<TbdSectionDto> sectionDtos = sections.stream().map(section -> {
            List<TbdWidgetRow> rows = rowsBySectionId.getOrDefault(section.getId(), new ArrayList<>());
            List<TbdWidgetRowDto> rowDtos = rows.stream().map(row -> {
                List<TbdWidget> widgets = widgetsByRowId.getOrDefault(row.getId(), new ArrayList<>());
                List<TbdWidgetDto> widgetDtos = widgets.stream()
                        .map(widget -> toWidgetDto(widget, kpisById))
                        .collect(Collectors.toList());
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

        TbdAssignationDto assignationDto = assignation.map(this::toAssignationDto).orElse(null);

        return TbdDashboardFullDto.builder()
                .id(dashboard.getId())
                .nom(dashboard.getNom())
                .titre(dashboard.getTitre())
                .sousTitre(dashboard.getSousTitre())
                .description(dashboard.getDescription())
                .sourceText(dashboard.getSourceText())
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
            Optional<CategorieAnalytique> assignation = categorieRepository.findByTbdDashboardId(d.getId());
            int nbSections = sectionRepository.findByDashboardIdAndActifTrue(d.getId()).size();
            return TbdDashboardSummaryDto.builder()
                    .id(d.getId())
                    .nom(d.getNom())
                    .titre(d.getTitre())
                    .sousTitre(d.getSousTitre())
                    .status(d.getStatus())
                    .actif(d.getActif())
                    .categorieAnalytiqueId(assignation.map(CategorieAnalytique::getId).orElse(null))
                    .assignationNom(assignation.map(this::buildCategoryDisplayName).orElse(null))
                    .nbSections(nbSections)
                    .lastModifiedDate(d.getLastModifiedDate())
                    .build();
        }).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TbdDashboardFullDto> findPublishedById(Long id) {
        return dashboardRepository.findById(id)
                .filter(d -> "PUBLISHED".equals(d.getStatus()) && Boolean.TRUE.equals(d.getActif()))
                .map(d -> findById(d.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TbdDashboardFullDto> findPublishedByCategorie(Long categorieId) {
        return categorieRepository.findById(categorieId)
                .map(CategorieAnalytique::getTbdDashboard)
                .filter(d -> d != null && "PUBLISHED".equals(d.getStatus()) && Boolean.TRUE.equals(d.getActif()))
                .map(d -> findById(d.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TbdDashboardSummaryDto> findAssignedByCategorieAdmin(Long categorieId) {
        return categorieRepository.findById(categorieId)
                .map(CategorieAnalytique::getTbdDashboard)
                .filter(d -> Boolean.TRUE.equals(d.getActif()))
                .map(d -> TbdDashboardSummaryDto.builder()
                        .id(d.getId())
                        .nom(d.getNom())
                        .titre(d.getTitre())
                        .sousTitre(d.getSousTitre())
                        .status(d.getStatus())
                        .actif(d.getActif())
                        .lastModifiedDate(d.getLastModifiedDate())
                        .build());
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
                .actif(true)
                .status("DRAFT")
                .build());

        duplicateSources(id, duplicatedDashboard.getId());
        duplicateSections(id, duplicatedDashboard.getId());
        return duplicatedDashboard;
    }

    @Override
    public List<Long> findAssignedCategoryIds(Long excludeDashboardId) {
        return categorieRepository.findByTbdDashboardIdIsNotNullOrderByLibelleAsc().stream()
                .filter(category -> excludeDashboardId == null
                        || category.getTbdDashboard() == null
                        || !excludeDashboardId.equals(category.getTbdDashboard().getId()))
                .map(CategorieAnalytique::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TbdDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        removeAssignation(id);
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
    public TbdAssignationDto assign(Long dashboardId, TbdDashboardAssignRequest request) {
        TbdDashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        CategorieAnalytique category = categorieRepository.findById(request.getCategorieAnalytiqueId())
                .orElseThrow(() -> new EntityNotFoundException("Categorie analytique non trouvée"));

        if (category.getTbdDashboard() != null && !dashboardId.equals(category.getTbdDashboard().getId())) {
            throw new IllegalArgumentException(CATEGORY_ALREADY_ASSIGNED);
        }

        categorieRepository.findByTbdDashboardId(dashboardId)
                .filter(existing -> !existing.getId().equals(category.getId()))
                .ifPresent(existing -> {
                    existing.setTbdDashboard(null);
                    categorieRepository.save(existing);
                });

        category.setTbdDashboard(dashboard);
        return toAssignationDto(categorieRepository.save(category));
    }

    @Override
    @Transactional
    public void removeAssignation(Long dashboardId) {
        categorieRepository.findByTbdDashboardId(dashboardId).ifPresent(category -> {
            category.setTbdDashboard(null);
            categorieRepository.save(category);
        });
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
        validateWidgetSourceForCreate(request);
        TbdWidget widget = TbdWidget.builder()
                .rowId(request.getRowId())
                .type(request.getType())
                .kpiId("KPI_CARD".equals(request.getType()) ? request.getKpiId() : null)
                .contentJson(request.getContentJson())
                .titre(request.getTitre())
                .ordre(request.getOrdre() != null ? request.getOrdre() : 0)
                .sizePercent(request.getSizePercent() != null ? request.getSizePercent() : 50)
                .actif(true)
                .build();
        if (request.getIndicateurId() != null) {
            indicateurRepository.findById(request.getIndicateurId()).ifPresent(widget::setIndicateur);
        }
        if ("KPI_CARD".equals(request.getType()) && request.getKpiId() != null) {
            chiffreCleRepository.findById(request.getKpiId())
                    .orElseThrow(() -> new EntityNotFoundException("KPI introuvable."));
            widget.setIndicateur(null);
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
        if (!"CHART".equals(widget.getType())) {
            throw new IllegalArgumentException("Seuls les widgets CHART peuvent etre relies a un indicateur.");
        }
        if (request.getIndicateurId() == null) {
            widget.setIndicateur(null);
        } else {
            widget.setIndicateur(indicateurRepository.findById(request.getIndicateurId())
                    .orElseThrow(() -> new EntityNotFoundException("Indicateur introuvable.")));
        }
        widget.setKpiId(null);
        widgetRepository.save(widget);
    }

    @Override
    @Transactional
    public void updateWidgetKpi(Long widgetId, TbdWidgetUpdateKpiRequest request) {
        TbdWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
        if (!"KPI_CARD".equals(widget.getType())) {
            throw new IllegalArgumentException("Seuls les widgets KPI_CARD peuvent etre relies a un KPI.");
        }
        if (request.getKpiId() == null) {
            widget.setKpiId(null);
        } else {
            ChiffreCle kpi = chiffreCleRepository.findById(request.getKpiId())
                    .orElseThrow(() -> new EntityNotFoundException("KPI introuvable."));
            widget.setKpiId(kpi.getId());
        }
        widget.setIndicateur(null);
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

    private TbdAssignationDto toAssignationDto(CategorieAnalytique category) {
        if (category == null) {
            return null;
        }
        String domaineNom = category.getDomaineAnalytique() != null
                ? (category.getDomaineAnalytique().getTitre() != null && !category.getDomaineAnalytique().getTitre().isBlank()
                        ? category.getDomaineAnalytique().getTitre()
                        : category.getDomaineAnalytique().getNom())
                : null;
        return TbdAssignationDto.builder()
                .cibleType("CATEGORIE_ANALYTIQUE")
                .cibleId(category.getId())
                .cibleNom(buildCategoryDisplayName(category))
                .domaineAnalytiqueId(category.getDomaineAnalytique() != null ? category.getDomaineAnalytique().getId() : null)
                .domaineAnalytiqueNom(domaineNom)
                .build();
    }

    private String buildCategoryDisplayName(CategorieAnalytique category) {
        if (category == null) {
            return null;
        }
        String domaineNom = category.getDomaineAnalytique() != null
                ? (category.getDomaineAnalytique().getTitre() != null && !category.getDomaineAnalytique().getTitre().isBlank()
                        ? category.getDomaineAnalytique().getTitre()
                        : category.getDomaineAnalytique().getNom())
                : null;
        String categorieNom = category.getLibelle() != null && !category.getLibelle().isBlank()
                ? category.getLibelle()
                : category.getNom();
        if (domaineNom == null || domaineNom.isBlank()) {
            return categorieNom;
        }
        return domaineNom + " - " + categorieNom;
    }

    private TbdWidgetDto toWidgetDto(TbdWidget widget, Map<Long, ChiffreCle> kpisById) {
        String indicateurNom = null;
        String indicateurTitre = null;
        Long indicateurId = null;
        if (widget.getIndicateur() != null) {
            indicateurId = widget.getIndicateur().getId();
            indicateurNom = widget.getIndicateur().getNom();
            indicateurTitre = widget.getIndicateur().getTitre();
        }
        ChiffreCleDto kpiDto = Optional.ofNullable(widget.getKpiId())
                .map(kpisById::get)
                .map(chiffreCleDtoMapper::mapToDto)
                .orElse(null);
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
                .kpi(kpiDto)
                .contentJson(widget.getContentJson())
                .build();
    }

    private void validateWidgetSourceForCreate(TbdWidgetCreateRequest request) {
        if ("KPI_CARD".equals(request.getType()) && request.getKpiId() == null) {
            throw new IllegalArgumentException("Un widget KPI_CARD doit etre lie a un KPI.");
        }
    }
}
