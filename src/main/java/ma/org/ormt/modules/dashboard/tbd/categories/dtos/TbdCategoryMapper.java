package ma.org.ormt.modules.dashboard.tbd.categories.dtos;

import java.util.List;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.tbd.categories.models.TbdCategory;

@Component
public class TbdCategoryMapper {

    public List<TbdCategoryDto> mapCategoriesToDtos(List<TbdCategory> categories) {
        return categories.stream().map(this::mapCategory).toList();
    }

    public TbdCategoryDto mapCategoryToDto(TbdCategory category) {
        return mapCategory(category);
    }

    private TbdCategoryDto mapCategory(TbdCategory category) {
        if (category == null) {
            return null;
        }
        TbdCategoryDto dto = new TbdCategoryDto();
        copyBase(category, dto);
        dto.setNom(category.getNom());
        dto.setLibelle(category.getLibelle());
        dto.setDescription(category.getDescription());
        dto.setOrdre(category.getOrdre());
        dto.setActif(category.getActif());
        dto.setTbDomaineId(category.getTbDomaine() != null ? category.getTbDomaine().getId() : null);
        dto.setTbDomaine(mapTBDomaine(category.getTbDomaine()));
        return dto;
    }

    private TBDomaineDto mapTBDomaine(TBDomaine tbDomaine) {
        if (tbDomaine == null) {
            return null;
        }
        TBDomaineDto dto = new TBDomaineDto();
        copyBase(tbDomaine, dto);
        dto.setNom(tbDomaine.getNom());
        dto.setLibelle(tbDomaine.getLibelle());
        dto.setDescription(tbDomaine.getDescription());
        dto.setActif(tbDomaine.getActif());
        return dto;
    }

    private void copyBase(ma.org.ormt.core.commun.base.entity.BaseEntity entity,
            ma.org.ormt.core.commun.base.dto.BaseDto dto) {
        dto.setId(entity.getId());
        dto.setStatusCode(entity.getStatusCode());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
    }
}
