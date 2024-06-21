package ma.org.ancfcc.pva.core.commun.base.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

public interface BaseMapper<T, DTO> {

    @Named("mapToDtoWithServices")
    DTO mapToDto(T source, @Context Object... services);

    @Named("mapListToDtoWithServices")
    List<DTO> mapToDto(List<T> list, @Context Object... services);

    @Named("mapSetToDtoWithServices")
    Set<DTO> mapToDto(Set<T> entity);

    T mapToEntity(DTO dto);

    List<T> mapToEntity(List<DTO> list);

    Set<T> mapToEntity(Set<DTO> set);

    T mapToEntity(@MappingTarget T entity, DTO dto);

    default <R> R findService(Object[] services, Class<R> serviceType) {
        for (Object service : services) {
            if (serviceType.isInstance(service)) {
                return serviceType.cast(service);
            }
        }
        return null;
    }

}