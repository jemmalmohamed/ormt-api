package ma.org.ormt.modules.dashboard.tableaubord.v2.services.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.repository.TBDomaineIndicateurRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.repositories.TBDomaineRepository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2CategorieRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2RequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2WidgetItemRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2WidgetRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2DataSourceType;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2Status;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2WidgetType;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Categorie;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Widget;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2WidgetItem;
import ma.org.ormt.modules.dashboard.tableaubord.v2.repositories.TableauBordV2CategorieRepository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.repositories.TableauBordV2Repository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.repositories.TableauBordV2WidgetRepository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.services.TableauBordV2Service;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.repositories.GrapheConfigurationRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TableauBordV2ServiceImpl implements TableauBordV2Service {

    private static final String DASHBOARD_NOT_FOUND = "Tableau de bord V2 non trouvé";
    private static final String WIDGET_NOT_FOUND = "Widget de tableau de bord V2 non trouvé";

    private final TableauBordV2Repository dashboardRepository;
    private final TableauBordV2CategorieRepository categorieRepository;
    private final TableauBordV2WidgetRepository widgetRepository;
    private final TBDomaineIndicateurRepository tbDomaineIndicateurRepository;
    private final TBDomaineRepository tbDomaineRepository;
    private final IndicateurRepository indicateurRepository;
    private final GrapheConfigurationRepository grapheConfigurationRepository;
    private final ChiffreCleRepository chiffreCleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TableauBordV2> findAll() {
        return dashboardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableauBordV2> findById(Long id) {
        return dashboardRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableauBordV2> findPublishedByNom(String nom) {
        return dashboardRepository.findByNomAndStatusAndActifTrue(nom, TableauBordV2Status.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauBordV2> findPublished() {
        return dashboardRepository
                .findByStatusAndActifTrueOrderByCategorieOrdreAscTitreAsc(TableauBordV2Status.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauBordV2> findPublishedByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return dashboardRepository.findByIdInAndStatusAndActifTrueOrderByCategorieOrdreAscTitreAsc(ids,
                TableauBordV2Status.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauBordV2Categorie> findActiveCategories() {
        return categorieRepository.findByActifTrueOrderByTbDomaineLibelleAscOrdreAscLibelleAsc();
    }

    @Override
    public void syncCategoriesFromLegacyDomaines() {
        syncCategoriesFromLegacyDomainesInternal();
    }

    @Override
    public TableauBordV2Categorie createCategorie(TableauBordV2CategorieRequestDto requestDto) {
        TableauBordV2Categorie categorie = new TableauBordV2Categorie();
        applyCategorieRequest(categorie, requestDto);
        return categorieRepository.save(categorie);
    }

    @Override
    public TableauBordV2Categorie updateCategorie(Long id, TableauBordV2CategorieRequestDto requestDto) {
        TableauBordV2Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de tableau de bord V2 non trouvée"));
        applyCategorieRequest(categorie, requestDto);
        return categorieRepository.save(categorie);
    }

    @Override
    public void deleteCategorie(Long id) {
        TableauBordV2Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de tableau de bord V2 non trouvée"));
        boolean hasActiveDashboard = dashboardRepository.existsByCategorieAndActifTrueAndStatusNotAndIdNot(
                categorie, TableauBordV2Status.ARCHIVED, -1L);
        if (hasActiveDashboard) {
            throw new IllegalStateException(
                    "Impossible de supprimer une catégorie contenant un tableau de bord dynamique actif.");
        }
        categorieRepository.delete(categorie);
    }

    @Override
    public TableauBordV2 create(TableauBordV2RequestDto requestDto) {
        TableauBordV2 dashboard = new TableauBordV2();
        applyDashboardRequest(dashboard, requestDto);
        return dashboardRepository.save(dashboard);
    }

    @Override
    public TableauBordV2 update(Long id, TableauBordV2RequestDto requestDto) {
        TableauBordV2 dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        applyDashboardRequest(dashboard, requestDto);
        return dashboardRepository.save(dashboard);
    }

    @Override
    public void delete(Long id) {
        if (!dashboardRepository.existsById(id)) {
            throw new EntityNotFoundException(DASHBOARD_NOT_FOUND);
        }
        dashboardRepository.deleteById(id);
    }

    @Override
    public TableauBordV2Widget createWidget(Long dashboardId, TableauBordV2WidgetRequestDto requestDto) {
        TableauBordV2 dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException(DASHBOARD_NOT_FOUND));
        TableauBordV2Widget widget = new TableauBordV2Widget();
        widget.setDashboard(dashboard);
        applyWidgetRequest(widget, requestDto);
        return widgetRepository.save(widget);
    }

    @Override
    public TableauBordV2Widget updateWidget(Long dashboardId, Long widgetId, TableauBordV2WidgetRequestDto requestDto) {
        TableauBordV2Widget widget = findWidgetInDashboard(dashboardId, widgetId);
        applyWidgetRequest(widget, requestDto);
        return widgetRepository.save(widget);
    }

    @Override
    public void deleteWidget(Long dashboardId, Long widgetId) {
        TableauBordV2Widget widget = findWidgetInDashboard(dashboardId, widgetId);
        widgetRepository.delete(widget);
    }

    @Override
    public List<TableauBordV2Widget> reorderWidgets(Long dashboardId, List<Long> widgetIds) {
        if (!dashboardRepository.existsById(dashboardId)) {
            throw new EntityNotFoundException(DASHBOARD_NOT_FOUND);
        }
        List<TableauBordV2Widget> widgets = widgetRepository.findByDashboardIdOrderByOrdreAscIdAsc(dashboardId);
        Map<Long, TableauBordV2Widget> widgetMap = widgets.stream()
                .collect(Collectors.toMap(TableauBordV2Widget::getId, Function.identity()));
        for (int i = 0; i < widgetIds.size(); i++) {
            TableauBordV2Widget widget = widgetMap.get(widgetIds.get(i));
            if (widget != null) {
                widget.setOrdre(i);
            }
        }
        return widgetRepository.saveAll(widgets);
    }

    private TableauBordV2Widget findWidgetInDashboard(Long dashboardId, Long widgetId) {
        TableauBordV2Widget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new EntityNotFoundException(WIDGET_NOT_FOUND));
        if (widget.getDashboard() == null || !dashboardId.equals(widget.getDashboard().getId())) {
            throw new EntityNotFoundException(WIDGET_NOT_FOUND);
        }
        return widget;
    }

    private void applyDashboardRequest(TableauBordV2 dashboard, TableauBordV2RequestDto requestDto) {
        dashboard.setNom(requestDto.getNom());
        dashboard.setTitre(requestDto.getTitre());
        dashboard.setSousTitre(requestDto.getSousTitre());
        dashboard.setDescription(requestDto.getDescription());
        dashboard.setSource(requestDto.getSource());
        dashboard.setPeriodeLabel(requestDto.getPeriodeLabel());
        dashboard.setActif(requestDto.getActif());
        dashboard.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : TableauBordV2Status.DRAFT);
        dashboard.setCategorie(resolveCategorie(requestDto.getCategorieId()));
        validateSingleActiveDashboardPerCategorie(dashboard);
        dashboard.setThemeJson(requestDto.getThemeJson());
        dashboard.setSettingsJson(requestDto.getSettingsJson());
    }

    private TableauBordV2Categorie resolveCategorie(Long categorieId) {
        if (categorieId == null) {
            return null;
        }
        return categorieRepository.findById(categorieId)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de tableau de bord V2 non trouvée"));
    }

    private void validateSingleActiveDashboardPerCategorie(TableauBordV2 dashboard) {
        if (dashboard.getCategorie() == null || !Boolean.TRUE.equals(dashboard.getActif())
                || dashboard.getStatus() == TableauBordV2Status.ARCHIVED) {
            return;
        }
        Long dashboardId = dashboard.getId() != null ? dashboard.getId() : -1L;
        boolean exists = dashboardRepository.existsByCategorieAndActifTrueAndStatusNotAndIdNot(
                dashboard.getCategorie(), TableauBordV2Status.ARCHIVED, dashboardId);
        if (exists) {
            throw new IllegalStateException("Cette catégorie possède déjà un tableau de bord dynamique actif.");
        }
    }

    private void applyCategorieRequest(TableauBordV2Categorie categorie, TableauBordV2CategorieRequestDto requestDto) {
        TBDomaine tbDomaine = tbDomaineRepository.findById(requestDto.getTbDomaineId())
                .orElseThrow(() -> new EntityNotFoundException("Domaine TB non trouvé"));
        String libelle = requestDto.getLibelle().trim();
        String nom = buildCategorieNom(tbDomaine.getId(), libelle);
        categorieRepository.findByTbDomaineAndNom(tbDomaine, nom)
                .filter(existing -> !existing.getId().equals(categorie.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Cette catégorie existe déjà dans ce domaine.");
                });
        categorie.setTbDomaine(tbDomaine);
        categorie.setLibelle(libelle);
        categorie.setNom(nom);
        categorie.setDescription(requestDto.getDescription());
        categorie.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        categorie.setActif(requestDto.getActif() != null ? requestDto.getActif() : true);
    }

    private void syncCategoriesFromLegacyDomainesInternal() {
        List<TBDomaineIndicateur> associations = tbDomaineIndicateurRepository
                .findCategorizedByDomaineOrderByDomaineAndOrdre();
        Map<String, TBDomaineIndicateur> categoriesByKey = new LinkedHashMap<>();
        for (TBDomaineIndicateur association : associations) {
            String categorie = association.getCategorie() != null ? association.getCategorie().trim() : "";
            if (association.getTbDomaine() == null || categorie.isBlank()) {
                continue;
            }
            String key = association.getTbDomaine().getId() + "::" + categorie.toLowerCase(Locale.ROOT);
            categoriesByKey.putIfAbsent(key, association);
        }
        for (TBDomaineIndicateur association : categoriesByKey.values()) {
            String libelle = association.getCategorie().trim();
            String nom = buildCategorieNom(association);
            TableauBordV2Categorie categorie = categorieRepository
                    .findByTbDomaineAndNom(association.getTbDomaine(), nom)
                    .orElseGet(TableauBordV2Categorie::new);
            categorie.setNom(nom);
            categorie.setLibelle(libelle);
            categorie.setTbDomaine(association.getTbDomaine());
            categorie.setOrdre(association.getOrdre() != null ? association.getOrdre() : 0);
            categorie.setActif(true);
            categorieRepository.save(categorie);
        }
    }

    private String buildCategorieNom(TBDomaineIndicateur association) {
        return buildCategorieNom(association.getTbDomaine().getId(), association.getCategorie().trim());
    }

    private String buildCategorieNom(Long tbDomaineId, String libelle) {
        String slug = libelle.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return "tbv2-" + tbDomaineId + "-" + slug;
    }

    private void applyWidgetRequest(TableauBordV2Widget widget, TableauBordV2WidgetRequestDto requestDto) {
        widget.setType(requestDto.getType());
        widget.setTitre(requestDto.getTitre());
        widget.setSousTitre(requestDto.getSousTitre());
        widget.setDescription(requestDto.getDescription());
        widget.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        widget.setSection(requestDto.getSection());
        widget.setX(requestDto.getX());
        widget.setY(requestDto.getY());
        widget.setW(requestDto.getW());
        widget.setH(requestDto.getH());
        widget.setConfigJson(requestDto.getConfigJson());
        widget.setStyleJson(requestDto.getStyleJson());
        widget.setActif(requestDto.getActif() != null ? requestDto.getActif() : true);
        widget.setDataSourceType(resolveDataSourceType(requestDto));
        widget.setIndicateur(resolveIndicateur(requestDto.getIndicateurId()));
        widget.setGrapheConfiguration(resolveGrapheConfiguration(requestDto));
        widget.setChiffreCle(resolveChiffreCle(requestDto.getChiffreCleId()));
        replaceItems(widget, requestDto.getItems());
    }

    private TableauBordV2DataSourceType resolveDataSourceType(TableauBordV2WidgetRequestDto requestDto) {
        if (requestDto.getDataSourceType() != null) {
            return requestDto.getDataSourceType();
        }
        if (requestDto.getType() == TableauBordV2WidgetType.CHART
                || requestDto.getType() == TableauBordV2WidgetType.PIVOT_TABLE) {
            return TableauBordV2DataSourceType.INDICATEUR;
        }
        if (requestDto.getType() == TableauBordV2WidgetType.KPI_CARD
                || requestDto.getType() == TableauBordV2WidgetType.KPI_GROUP) {
            return requestDto.getChiffreCleId() != null ? TableauBordV2DataSourceType.CHIFFRE_CLE
                    : TableauBordV2DataSourceType.STATIC;
        }
        return TableauBordV2DataSourceType.NONE;
    }

    private Indicateur resolveIndicateur(Long indicateurId) {
        if (indicateurId == null) {
            return null;
        }
        return indicateurRepository.findById(indicateurId)
                .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé"));
    }

    private GrapheConfiguration resolveGrapheConfiguration(TableauBordV2WidgetRequestDto requestDto) {
        if (requestDto.getGrapheConfigurationId() != null) {
            return grapheConfigurationRepository.findById(requestDto.getGrapheConfigurationId())
                    .orElseThrow(() -> new EntityNotFoundException("Configuration graphe non trouvée"));
        }
        if (requestDto.getType() == TableauBordV2WidgetType.CHART && requestDto.getIndicateurId() != null) {
            List<GrapheConfiguration> defaults = grapheConfigurationRepository
                    .findByIndicateurIdAndIsDefaultTrue(requestDto.getIndicateurId());
            return defaults.isEmpty() ? null : defaults.get(0);
        }
        return null;
    }

    private ChiffreCle resolveChiffreCle(Long chiffreCleId) {
        if (chiffreCleId == null) {
            return null;
        }
        return chiffreCleRepository.findById(chiffreCleId)
                .orElseThrow(() -> new EntityNotFoundException("Chiffre clé non trouvé"));
    }

    private void replaceItems(TableauBordV2Widget widget, List<TableauBordV2WidgetItemRequestDto> requestItems) {
        widget.getItems().clear();
        if (requestItems == null) {
            return;
        }
        List<TableauBordV2WidgetItem> items = new ArrayList<>();
        for (int index = 0; index < requestItems.size(); index++) {
            TableauBordV2WidgetItemRequestDto requestItem = requestItems.get(index);
            TableauBordV2WidgetItem item = new TableauBordV2WidgetItem();
            item.setWidget(widget);
            item.setLibelle(requestItem.getLibelle());
            item.setValeur(requestItem.getValeur());
            item.setUnite(requestItem.getUnite());
            item.setDescription(requestItem.getDescription());
            item.setOrdre(requestItem.getOrdre() != null ? requestItem.getOrdre() : index);
            item.setConfigJson(requestItem.getConfigJson());
            item.setStyleJson(requestItem.getStyleJson());
            item.setActif(requestItem.getActif() != null ? requestItem.getActif() : true);
            items.add(item);
        }
        widget.getItems().addAll(items);
    }
}
