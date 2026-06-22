package ma.org.ormt.modules.dashboard.tbd.categories.services;

import java.util.List;

import ma.org.ormt.modules.dashboard.tbd.categories.dtos.request.TbdCategoryRequestDto;
import ma.org.ormt.modules.dashboard.tbd.categories.models.TbdCategory;

public interface TbdCategoryService {

    List<TbdCategory> findActiveCategories();

    void syncCategoriesFromDomaines();

    TbdCategory createCategory(TbdCategoryRequestDto requestDto);

    TbdCategory updateCategory(Long id, TbdCategoryRequestDto requestDto);

    void deleteCategory(Long id);

    void reorderCategories(List<ReorderItem> items);

    record ReorderItem(Long id, Integer ordre) {}
}
