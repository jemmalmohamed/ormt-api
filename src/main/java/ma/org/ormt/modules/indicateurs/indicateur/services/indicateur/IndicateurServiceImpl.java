package ma.org.ormt.modules.indicateurs.indicateur.services.indicateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurCrudDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurPivotDataTable;
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

}