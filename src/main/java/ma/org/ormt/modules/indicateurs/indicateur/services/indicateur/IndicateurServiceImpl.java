package ma.org.ormt.modules.indicateurs.indicateur.services.indicateur;

import java.util.Optional;

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
        indicateur.setDescription(entityToUpdate.getDescription());
        indicateur.setAbreviation(entityToUpdate.getAbreviation());
        indicateur.setActif(entityToUpdate.getActif());
        indicateur.setTypeTb(entityToUpdate.getTypeTb());
        indicateur.setUnite(entityToUpdate.getUnite());

        Source source = sourceService.findById(entityToUpdate.getSource().getId())
                .orElseThrow(() -> new EntityNotFoundException("Source not found"));
        indicateur.setSource(source);
        indicateur.setTypeGraphe(entityToUpdate.getTypeGraphe());
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
        IndicateurDetailDto dto = indicateurDetailMapper.mapToDto(indicateur);

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
                dto.setCreateTemplateData(
                        IndicateurCrudDataTable.buildCreateTemplateData(indicateur));
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
                dto.setCreateTemplateData(
                        IndicateurCrudDataTable.buildCreateTemplateData(indicateur));
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

}