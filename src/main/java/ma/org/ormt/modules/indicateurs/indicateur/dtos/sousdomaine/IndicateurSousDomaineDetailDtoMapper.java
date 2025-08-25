package ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Mapper
public interface IndicateurSousDomaineDetailDtoMapper
        extends BaseDtoMapper<Indicateur, IndicateurSousDomaineDetailDto> {

    // @AfterMapping
    // default void setHasDonnees(Indicateur source, @MappingTarget
    // IndicateurSousDomaineDetailDto target) {
    // // Set hasDonnees to true if the list is not null and not empty
    // target.setHasDonnees(source.getDonnees() != null &&
    // !source.getDonnees().isEmpty());

    // target.setNombreDimensions(source.getDimensions() != null ?
    // source.getDimensions().size() : 0);

    // }

}