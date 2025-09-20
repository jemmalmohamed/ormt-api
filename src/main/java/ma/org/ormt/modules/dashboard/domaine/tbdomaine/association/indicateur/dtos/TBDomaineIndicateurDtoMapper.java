package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Mapper
public interface TBDomaineIndicateurDtoMapper extends BaseDtoMapper<TBDomaineIndicateur, TBDomaineIndicateurDto> {

    @AfterMapping
    default void mapNestedIndicateur(TBDomaineIndicateur source, @MappingTarget TBDomaineIndicateurDto target,
            @Context Object... services) {
        // If there's an indicateur in the source, re-map it with services context
        if (source.getIndicateur() != null && target.getIndicateur() != null) {
            IndicateurService indicateurService = findService(services, IndicateurService.class);

            if (indicateurService != null) {
                // Find the nested mapper and re-map with services
                IndicateurSousDomaineDetailDtoMapper nestedMapper = org.mapstruct.factory.Mappers
                        .getMapper(IndicateurSousDomaineDetailDtoMapper.class);
                target.setIndicateur(nestedMapper.mapToDto(source.getIndicateur(), indicateurService));
            }
        }
    }
}