package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.TBDomaineIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Mapper
public interface TBDomaineDetailDtoMapper extends BaseDtoMapper<TBDomaine, TBDomaineDetailDto> {

    @AfterMapping
    default void mapNestedAssociations(TBDomaine source, @MappingTarget TBDomaineDetailDto target,
            @Context Object... services) {
        // Re-map the associated indicateurs with services context
        if (source.getTbDomaineIndicateurs() != null && target.getTbDomaineIndicateurs() != null) {
            IndicateurService indicateurService = findService(services, IndicateurService.class);

            if (indicateurService != null) {
                TBDomaineIndicateurDtoMapper associationMapper = org.mapstruct.factory.Mappers
                        .getMapper(TBDomaineIndicateurDtoMapper.class);

                // Re-map each association with services context
                for (int i = 0; i < source.getTbDomaineIndicateurs().size()
                        && i < target.getTbDomaineIndicateurs().size(); i++) {
                    target.getTbDomaineIndicateurs().set(i,
                            associationMapper.mapToDto(source.getTbDomaineIndicateurs().get(i), indicateurService));
                }
            }
        }
    }
}
