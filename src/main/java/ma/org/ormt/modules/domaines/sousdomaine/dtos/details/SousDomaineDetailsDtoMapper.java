package ma.org.ormt.modules.domaines.sousdomaine.dtos.details;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Mapper(uses = { IndicateurSousDomaineDetailDtoMapper.class })
public interface SousDomaineDetailsDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineDetailsDto> {

    @AfterMapping
    default void mapNestedAssociations(SousDomaine source, @MappingTarget SousDomaineDetailsDto target,
            @Context Object... services) {
        // Re-map the associated indicateurs with services context
        if (source.getIndicateurs() != null && target.getIndicateurs() != null) {
            IndicateurService indicateurService = findService(services, IndicateurService.class);

            if (indicateurService != null) {
                IndicateurSousDomaineDetailDtoMapper associationMapper = org.mapstruct.factory.Mappers
                        .getMapper(IndicateurSousDomaineDetailDtoMapper.class);

                // Re-map each association with services context
                for (int i = 0; i < source.getIndicateurs().size()
                        && i < target.getIndicateurs().size(); i++) {
                    target.getIndicateurs().set(i,
                            associationMapper.mapToDto(source.getIndicateurs().get(i), indicateurService));
                }
            }
        }
    }

}
