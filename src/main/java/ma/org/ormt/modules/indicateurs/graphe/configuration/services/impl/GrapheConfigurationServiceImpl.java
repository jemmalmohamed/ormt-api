package ma.org.ormt.modules.indicateurs.graphe.configuration.services.impl;

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
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request.GrapheConfigurationRequestDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request.GrapheConfigurationRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.repositories.GrapheConfigurationRepository;
import ma.org.ormt.modules.indicateurs.graphe.configuration.services.GrapheConfigurationService;
import ma.org.ormt.modules.indicateurs.graphe.type.services.GrapheTypeService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
@Transactional
public class GrapheConfigurationServiceImpl extends BaseServiceImpl<GrapheConfiguration>
        implements GrapheConfigurationService {

    @Autowired
    private GrapheConfigurationRepository grapheConfigurationRepository;
    @Autowired
    private IndicateurService indicateurService;
    @Autowired
    private GrapheTypeService grapheTypeService;

    @Autowired
    private ObjectsValidator<GrapheConfigurationRequestDto> validator;

    @Autowired
    private GrapheConfigurationRequestDtoMapper grapheConfigurationRequestMapper;

    private static final String NOT_FOUND_STRING = "GrapheConfiguration non trouvée";

    public GrapheConfigurationServiceImpl(GrapheConfigurationRepository grapheConfigurationRepository,
            SpecificationService specificationService) {
        super(grapheConfigurationRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return grapheConfigurationRepository.existsById(id);
    }

    @Override
    public Optional<GrapheConfiguration> findByNom(String nom) {
        return grapheConfigurationRepository.findByNom(nom);
    }

    @Override
    public Page<GrapheConfiguration> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), GrapheConfiguration.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<GrapheConfiguration> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), GrapheConfiguration.class);
        return findAll(specification, pageable);
    }

    @Override
    public GrapheConfiguration save(GrapheConfiguration grapheConfiguration) {
        return grapheConfigurationRepository.save(grapheConfiguration);
    }

    @Override
    public GrapheConfiguration create(GrapheConfigurationRequestDto requestDto) {
        try {
            validator.validate(requestDto);
            GrapheConfiguration grapheConfigurationToCreate = grapheConfigurationRequestMapper
                    .mapToEntity(requestDto);

            grapheConfigurationToCreate.setIndicateur(
                    indicateurService.findById(requestDto.getIndicateur().getId())
                            .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé")));

            grapheConfigurationToCreate.setGrapheType(
                    grapheTypeService.findById(requestDto.getGrapheType().getId())
                            .orElseThrow(() -> new EntityNotFoundException("GrapheType non trouvé")));

            grapheConfigurationToCreate.setDimensionMappingJson("{}");

            return grapheConfigurationRepository.save(grapheConfigurationToCreate);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erreur lors de la création de la grapheConfiguration: " + e.getMessage());
        }
    }

    @Override
    public GrapheConfiguration update(Long id, GrapheConfigurationRequestDto requestDto) {
        try {
            validator.validate(requestDto);
            checkPathId(id, requestDto.getId());

            GrapheConfiguration grapheConfiguration = grapheConfigurationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

            updateFields(grapheConfiguration, requestDto);

            return grapheConfigurationRepository.save(grapheConfiguration);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erreur lors de la mise à jour de la grapheConfiguration: " + e.getMessage());
        }
    }

    private void updateFields(GrapheConfiguration grapheConfiguration,
            GrapheConfigurationRequestDto entityToUpdate) {
        grapheConfiguration.setNom(entityToUpdate.getNom());
        grapheConfiguration.setDimensionMappingJson("{}");
        grapheConfiguration.setChartOptionsJson(entityToUpdate.getChartOptionsJson());
        grapheConfiguration.setIsDefault(entityToUpdate.getIsDefault());
        grapheConfiguration.setIndicateur(
                indicateurService.findById(entityToUpdate.getIndicateur().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé")));
        grapheConfiguration.setGrapheType(
                grapheTypeService.findById(entityToUpdate.getGrapheType().getId())
                        .orElseThrow(() -> new EntityNotFoundException("GrapheType non trouvé")));

    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateGrapheConfigurationDependencies(id);
    }

    private void validateGrapheConfigurationDependencies(Long id) {

    }

    @Override
    public Optional<GrapheConfiguration> findByIndicateurAndGrapheType(Long indicateurId, String grapheType) {
        return grapheConfigurationRepository.findByIndicateurAndGrapheType(
                indicateurService.findById(indicateurId)
                        .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé")),
                grapheTypeService.findByNom(grapheType)
                        .orElseThrow(() -> new EntityNotFoundException("GrapheType non trouvé")));
    }

}