package ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Mapper
public interface IndicateurSousDomaineDetailDtoMapper
        extends BaseDtoMapper<Indicateur, IndicateurSousDomaineDetailDto> {

    @AfterMapping
    default void setTerritoireInfo(Indicateur source, @MappingTarget IndicateurSousDomaineDetailDto target,
            @Context Object... services) {
        // Find the IndicateurService to analyze territorial status
        IndicateurService indicateurService = findService(services, IndicateurService.class);

        if (indicateurService != null) {
            // Get the territorial status
            String territoireStatus = indicateurService.analyzeTerritoireStatus(source);
            target.setTerritoire(territoireStatus);

            // Set regional to true if it's not "National" or "Pas de dimensions"
            target.setRegional(territoireStatus != null &&
                    !territoireStatus.equals("National") &&
                    !territoireStatus.equals("Pas de dimensions"));
        } else {
            // Fallback: set default values if service is not available
            target.setTerritoire("Unknown");
            target.setRegional(false);
        }
    }

}