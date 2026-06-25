package ma.org.ormt.modules.indicateurs.indicateur.services.indicateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.request.IndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.request.IndicateurRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.link.IndicateurLinkedAnalyticsCategoryDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.link.IndicateurLinkedDashboardDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurCrudDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurPivotDataTable;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSectionRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRowRepository;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

@Service
public class IndicateurServiceImpl extends BaseServiceImpl<Indicateur> implements IndicateurService {

    @Autowired
    private IndicateurRepository indicateurRepository;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private ObjectsValidator<IndicateurRequestDto> validator;

    @Autowired
    private IndicateurRequestDtoMapper indicateurRequestMapper;

    @Autowired
    private IndicateurDetailDtoMapper indicateurDetailMapper;

    @Autowired
    private TbdWidgetRepository tbdWidgetRepository;

    @Autowired
    private TbdWidgetRowRepository tbdWidgetRowRepository;

    @Autowired
    private TbdSectionRepository tbdSectionRepository;

    @Autowired
    private TbdDashboardRepository tbdDashboardRepository;

    @Autowired
    private ChiffreCleRepository chiffreCleRepository;

    @Autowired
    private CategorieAnalytiqueRepository categorieAnalytiqueRepository;

    static final String NOT_FOUND_STRING = "Indicateur not found";
    static final String SOUS_DOMAINE_NOT_FOUND = "SousDomaine not found";

    public IndicateurServiceImpl(IndicateurRepository indicateurRepository, SpecificationService specificationService) {
        super(indicateurRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return indicateurRepository.existsById(id);
    }

    @Override
    public Optional<Indicateur> findByNom(String nom) {
        return indicateurRepository.findByNom(nom.toLowerCase());
    }

    @Override
    public Page<Indicateur> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Indicateur.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Indicateur> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Indicateur.class);

        return findAll(specification, pageable);
    }

    @Override
    public Indicateur create(IndicateurRequestDto requestDto) {
        validator.validate(requestDto);

        Indicateur indicateurToCreate = indicateurRequestMapper.mapToEntity(requestDto);
        Source source = sourceService.findById(requestDto.getSource().getId())
                .orElseThrow(() -> new EntityNotFoundException("Source not found"));
        indicateurToCreate.setSource(source);
        return indicateurRepository.save(indicateurToCreate);
    }

    @Override
    public Indicateur update(Long id, IndicateurRequestDto requestDto) {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());

        Indicateur indicateur = indicateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(indicateur, requestDto);

        return indicateurRepository.save(indicateur);
    }

    @Override
    public Indicateur save(Indicateur indicateur) {
        return indicateurRepository.save(indicateur);
    }

    private void updateFields(Indicateur indicateur, IndicateurRequestDto entityToUpdate) {
        indicateur.setNom(entityToUpdate.getNom());
        indicateur.setTitre(entityToUpdate.getTitre());
        indicateur.setDescription(entityToUpdate.getDescription());
        indicateur.setAbreviation(entityToUpdate.getAbreviation());
        indicateur.setActif(entityToUpdate.getActif());
        indicateur.setTypeTb(entityToUpdate.getTypeTb());
        indicateur.setUnite(entityToUpdate.getUnite());

        Source source = sourceService.findById(entityToUpdate.getSource().getId())
                .orElseThrow(() -> new EntityNotFoundException("Source not found"));
        indicateur.setSource(source);
        // indicateur.setTypeGraphe(entityToUpdate.getTypeGraphe());
        indicateur.setCategorie(entityToUpdate.getCategorie());
        indicateur.setRegleCalcul(entityToUpdate.getRegleCalcul());
    }

    @Override
    public Optional<Indicateur> findByNomWithDonnees(String nom) {
        return indicateurRepository.findByNomWithDonnees(nom.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Indicateur> findByNomWithDonneesAndDimensions(String nom) {
        Optional<Indicateur> indicateurOpt = indicateurRepository.findByNomWithDonnees(nom.toLowerCase());

        if (indicateurOpt.isPresent()) {
            Indicateur indicateur = indicateurOpt.get();
            // Force initialization of the indicateurDimensions collection within the
            // transaction
            org.hibernate.Hibernate.initialize(indicateur.getIndicateurDimensions());

            // Force initialization of the valeurDimensions collections within each
            // DonneeIndicateur
            if (indicateur.getDonnees() != null) {
                for (var donnee : indicateur.getDonnees()) {
                    org.hibernate.Hibernate.initialize(donnee.getValeurDimensions());
                }
            }

            return Optional.of(indicateur);
        }

        return Optional.empty();
    }

    @Override
    public IndicateurDetailDto getIndicateurWithTableData(Long id, String tableFormat) {

        Indicateur indicateur = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indicateur not found with id: " + id));

        // Use the existing mapper to convert to DTO
        IndicateurDetailDto dto = indicateurDetailMapper.mapToDto(indicateur, this);

        // Check if indicateur has valid data for table processing
        if (indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty() ||
                indicateur.getIndicateurDimensions() == null || indicateur.getIndicateurDimensions().isEmpty()) {
            // Return DTO with empty table data structures
            return dto;
        }

        // Add table data based on request format with error handling
        try {
            if ("pivot".equals(tableFormat)) {
                dto.setPivotTableData(
                        IndicateurPivotDataTable.buildPivotTableWithMetadata(indicateur));
            } else if ("flat".equals(tableFormat)) {
                dto.setFlatTableData(
                        IndicateurFlatDataTable.buildFlatTableData(indicateur));
            } else if ("crud".equals(tableFormat)) {
                dto.setCrudTableData(
                        IndicateurCrudDataTable.buildCrudTableData(indicateur));
            } else if ("create".equals(tableFormat)) {
                // dto.setCreateTemplateData(
                // IndicateurCrudDataTable.buildCreateTemplateData(indicateur));
            } else if ("both".equals(tableFormat)) {
                dto.setPivotTableData(
                        IndicateurPivotDataTable.buildPivotTableWithMetadata(indicateur));
                dto.setFlatTableData(
                        IndicateurFlatDataTable.buildFlatTableData(indicateur));
            } else if ("all".equals(tableFormat)) {
                dto.setPivotTableData(
                        IndicateurPivotDataTable.buildPivotTableWithMetadata(indicateur));
                dto.setFlatTableData(
                        IndicateurFlatDataTable.buildFlatTableData(indicateur));
                dto.setCrudTableData(
                        IndicateurCrudDataTable.buildCrudTableData(indicateur));

            }
        } catch (ArithmeticException e) {
            // Log the division by zero error and return DTO with basic info
            System.err.println("Arithmetic error (division by zero) when building table data for indicateur " + id
                    + ": " + e.getMessage());
            // Table data fields will remain null/empty, but the basic DTO info is preserved
        } catch (Exception e) {
            // Log any other unexpected errors
            System.err
                    .println("Unexpected error when building table data for indicateur " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return dto;
    }

    @Override
    public List<IndicateurDetailDto> getIndicateurListWithTableData(String tableFormat) {
        List<Indicateur> indicateurs = findAll();
        List<IndicateurDetailDto> dtos = new ArrayList<>();

        for (Indicateur indicateur : indicateurs) {
            IndicateurDetailDto dto = indicateurDetailMapper.mapToDto(indicateur);
            // Add table data based on request format
            if ("pivot".equals(tableFormat)) {
                dto.setPivotTableData(IndicateurPivotDataTable.buildPivotTableWithMetadata(indicateur));
            }
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public String analyzeTerritoireStatus(Indicateur indicateur) {
        // Récupérer les dimensions de l'indicateur
        List<Dimension> dimensions = indicateur.getDimensions();

        if (dimensions == null || dimensions.isEmpty()) {
            return "Pas de dimensions";
        }

        // Chercher une dimension de type "région" ou "territoire"
        boolean hasRegionDimension = dimensions.stream()
                .anyMatch(dimension -> dimension.getNom() != null &&
                        (dimension.getNom().toLowerCase().contains("region")));

        if (!hasRegionDimension) {
            return "National";
        }

        // Si on a une dimension région/territoire, vérifier les données
        if (indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty()) {
            return "Régional mais pas de données";
        }

        // Récupérer toutes les valeurs pour les dimensions région de cet indicateur
        List<String> valeursRegionales = indicateur.getDonnees().stream()
                .flatMap(donnee -> donnee.getValeurDimensions().stream())
                .filter(vd -> vd.getDimension() != null &&
                        vd.getDimension().getNom() != null &&
                        vd.getDimension().getNom().toLowerCase().contains("region"))
                .map(vd -> vd.getValeur())
                .filter(valeur -> valeur != null && !valeur.trim().isEmpty())
                .map(valeur -> valeur.toLowerCase().trim())
                .distinct()
                .collect(Collectors.toList());

        if (valeursRegionales.isEmpty()) {
            return "Régional mais pas de données";
        }

        // Vérifier spécifiquement si Marrakech est présent
        boolean hasMarrakech = valeursRegionales.stream()
                .anyMatch(valeur -> valeur.contains("marrakech"));

        if (hasMarrakech) {
            // Marrakech trouvé - le mentionner en premier
            List<String> autresRegions = valeursRegionales.stream()
                    .filter(valeur -> !valeur.contains("marrakech"))
                    .filter(valeur -> Arrays.asList("casablanca", "rabat", "fès", "tanger",
                            "agadir", "meknès", "oujda", "kenitra", "tétouan",
                            "casablanca - settat", "rabat - salé - kénitra",
                            "fès - meknès", "tanger - tétouan - al hoceima").stream()
                            .anyMatch(region -> valeur.contains(region)))
                    .limit(2) // Limiter à 2 autres régions
                    .collect(Collectors.toList());

            String regionsText = "marrakech";
            if (!autresRegions.isEmpty()) {
                regionsText += ", " + String.join(", ", autresRegions);
                if (valeursRegionales.size() > autresRegions.size() + 1) {
                    regionsText += "...";
                }
            }

            return "Régional (" + regionsText + ")";
        } else {
            // Pas de Marrakech - vérifier autres régions marocaines
            List<String> regionsMarocaines = Arrays.asList(
                    "casablanca", "rabat", "fès", "tanger",
                    "agadir", "meknès", "oujda", "kenitra", "tétouan",
                    "casablanca - settat", "rabat - salé - kénitra",
                    "fès - meknès", "tanger - tétouan - al hoceima");

            List<String> regionsPresentes = valeursRegionales.stream()
                    .filter(valeur -> regionsMarocaines.stream()
                            .anyMatch(region -> valeur.contains(region)))
                    .collect(Collectors.toList());

            if (!regionsPresentes.isEmpty()) {
                String regionsText = regionsPresentes.stream()
                        .limit(3)
                        .collect(Collectors.joining(", "));

                if (regionsPresentes.size() > 3) {
                    regionsText += "...";
                }

                return "Régional (pas de données Marrakech, " + regionsText + ")";
            } else {
                return "Régional (pas de données Marrakech, autres territoires)";
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndicateurLinkedAnalyticsCategoryDto> resolveLinkedAnalyticsCategories(Indicateur indicateur) {
        DerivedAnalyticsLinks derivedLinks = buildDerivedAnalyticsLinks(indicateur);
        return derivedLinks.categories().values().stream()
                .sorted(Comparator
                        .comparing((IndicateurLinkedAnalyticsCategoryDto item) -> defaultText(item.getDomaineAnalytiqueTitre(), item.getDomaineAnalytiqueNom()))
                        .thenComparing(item -> defaultText(item.getCategorieAnalytiqueLibelle(), item.getCategorieAnalytiqueNom())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndicateurLinkedDashboardDto> resolveLinkedDashboards(Indicateur indicateur) {
        DerivedAnalyticsLinks derivedLinks = buildDerivedAnalyticsLinks(indicateur);
        return derivedLinks.dashboards().values().stream()
                .sorted(Comparator
                        .comparing((IndicateurLinkedDashboardDto item) -> defaultText(item.getTitre(), item.getNom()))
                        .thenComparing(item -> item.getId() != null ? item.getId() : Long.MAX_VALUE))
                .toList();
    }

    private DerivedAnalyticsLinks buildDerivedAnalyticsLinks(Indicateur indicateur) {
        Long indicateurId = indicateur != null ? indicateur.getId() : null;
        if (indicateurId == null) {
            return new DerivedAnalyticsLinks(new LinkedHashMap<>(), new LinkedHashMap<>());
        }

        List<TbdWidget> directWidgets = tbdWidgetRepository.findByIndicateurId(indicateurId);
        List<ChiffreCle> chiffres = chiffreCleRepository.findByIndicateurId(indicateurId);
        List<Long> kpiIds = chiffres.stream()
                .map(ChiffreCle::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<TbdWidget> kpiWidgets = kpiIds.isEmpty() ? List.of() : tbdWidgetRepository.findByKpiIdIn(kpiIds);

        List<TbdWidget> widgets = Stream.concat(directWidgets.stream(), kpiWidgets.stream())
                .filter(Objects::nonNull)
                .filter(widget -> Boolean.TRUE.equals(widget.getActif()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(TbdWidget::getId, widget -> widget, (left, right) -> left, LinkedHashMap::new),
                        map -> new ArrayList<>(map.values())));

        if (widgets.isEmpty()) {
            return new DerivedAnalyticsLinks(new LinkedHashMap<>(), new LinkedHashMap<>());
        }

        List<Long> rowIds = widgets.stream()
                .map(TbdWidget::getRowId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, TbdWidgetRow> rowsById = tbdWidgetRowRepository.findByIdIn(rowIds).stream()
                .collect(Collectors.toMap(TbdWidgetRow::getId, row -> row));

        List<Long> sectionIds = rowsById.values().stream()
                .map(TbdWidgetRow::getSectionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, TbdSection> sectionsById = tbdSectionRepository.findByIdIn(sectionIds).stream()
                .collect(Collectors.toMap(TbdSection::getId, section -> section));

        List<Long> dashboardIds = sectionsById.values().stream()
                .map(TbdSection::getDashboardId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, TbdDashboard> dashboardsById = tbdDashboardRepository.findAllById(dashboardIds).stream()
                .collect(Collectors.toMap(TbdDashboard::getId, dashboard -> dashboard));

        Map<Long, CategorieAnalytique> categoriesByDashboardId = dashboardIds.isEmpty()
                ? Map.of()
                : categorieAnalytiqueRepository.findByTbdDashboardIdInWithDomain(dashboardIds).stream()
                        .filter(category -> category.getTbdDashboard() != null && category.getTbdDashboard().getId() != null)
                        .collect(Collectors.toMap(
                                category -> category.getTbdDashboard().getId(),
                                category -> category,
                                (left, right) -> left,
                                LinkedHashMap::new));

        LinkedHashMap<Long, IndicateurLinkedDashboardDto> dashboards = new LinkedHashMap<>();
        for (Long dashboardId : dashboardIds) {
            TbdDashboard dashboard = dashboardsById.get(dashboardId);
            if (dashboard == null) {
                continue;
            }
            CategorieAnalytique category = categoriesByDashboardId.get(dashboardId);
            dashboards.put(dashboardId, IndicateurLinkedDashboardDto.builder()
                    .id(dashboard.getId())
                    .nom(dashboard.getNom())
                    .titre(dashboard.getTitre())
                    .status(dashboard.getStatus())
                    .actif(dashboard.getActif())
                    .lastModifiedDate(dashboard.getLastModifiedDate())
                    .categorieAnalytiqueId(category != null ? category.getId() : null)
                    .categorieAnalytiqueLibelle(category != null ? category.getLibelle() : null)
                    .build());
        }

        LinkedHashMap<Long, IndicateurLinkedAnalyticsCategoryDto> categories = new LinkedHashMap<>();
        for (CategorieAnalytique category : categoriesByDashboardId.values()) {
            if (category.getId() == null) {
                continue;
            }
            categories.put(category.getId(), IndicateurLinkedAnalyticsCategoryDto.builder()
                    .categorieAnalytiqueId(category.getId())
                    .categorieAnalytiqueNom(category.getNom())
                    .categorieAnalytiqueLibelle(category.getLibelle())
                    .domaineAnalytiqueId(category.getDomaineAnalytique() != null ? category.getDomaineAnalytique().getId() : null)
                    .domaineAnalytiqueNom(category.getDomaineAnalytique() != null ? category.getDomaineAnalytique().getNom() : null)
                    .domaineAnalytiqueTitre(category.getDomaineAnalytique() != null ? category.getDomaineAnalytique().getTitre() : null)
                    .tbdDashboardId(category.getTbdDashboard() != null ? category.getTbdDashboard().getId() : null)
                    .build());
        }

        return new DerivedAnalyticsLinks(categories, dashboards);
    }

    private String defaultText(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "";
    }

    private record DerivedAnalyticsLinks(
            Map<Long, IndicateurLinkedAnalyticsCategoryDto> categories,
            Map<Long, IndicateurLinkedDashboardDto> dashboards) {
    }

}
