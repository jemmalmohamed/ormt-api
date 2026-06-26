package ma.org.ormt.modules.indicateurs.indicateur.dtos.detail;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Mapper
public interface IndicateurDetailDtoMapper extends BaseDtoMapper<Indicateur, IndicateurDetailDto> {

    @AfterMapping
    default void setHasDonnees(Indicateur source, @MappingTarget IndicateurDetailDto target,
            @Context Object... services) {
        // Set hasDonnees to true if the list is not null and not empty
        target.setHasDonnees(source.getDonnees() != null && !source.getDonnees().isEmpty());

        target.setNombreDimensions(source.getDimensions() != null ? source.getDimensions().size() : 0);
        // Find the IndicateurService to analyze territorial status
        IndicateurService indicateurService = findService(services, IndicateurService.class);

        if (indicateurService != null) {
            // Get the territorial status
            String territoireStatus = indicateurService.analyzeTerritoireStatus(source);
            target.setTerritoire(territoireStatus);
            target.setLinkedAnalyticsCategories(indicateurService.resolveLinkedAnalyticsCategories(source));
            target.setLinkedDashboards(indicateurService.resolveLinkedDashboards(source));

            // Set regional to true if it's not "national" or "Pas de dimensions"
            target.setRegional(territoireStatus != null &&
                    !territoireStatus.equals("national") &&
                    !territoireStatus.equals("Pas de dimensions"));
        } else {
            // Fallback: set default values if service is not available
            target.setTerritoire("national");
            target.setRegional(false);
            target.setLinkedAnalyticsCategories(java.util.List.of());
            target.setLinkedDashboards(java.util.List.of());
        }
    }

}
