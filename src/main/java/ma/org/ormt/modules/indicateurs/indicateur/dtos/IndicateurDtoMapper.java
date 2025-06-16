package ma.org.ormt.modules.indicateurs.indicateur.dtos;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Mapper
public interface IndicateurDtoMapper extends BaseDtoMapper<Indicateur, IndicateurDto> {

    @AfterMapping
    default void setHasDonnees(Indicateur source, @MappingTarget IndicateurDto target) {
        // Set hasDonnees to true if the list is not null and not empty
        target.setHasDonnees(source.getDonnees() != null && !source.getDonnees().isEmpty());
    }
}