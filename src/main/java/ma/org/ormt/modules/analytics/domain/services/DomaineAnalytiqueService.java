package ma.org.ormt.modules.analytics.domain.services;

import java.util.List;
import java.util.Optional;

import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytiqueSection;
import ma.org.ormt.modules.analytics.domain.dtos.PortailAnalytiqueTransitionSummaryDto;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytiqueSection;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueLinkRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.ReorderByIdItem;

public interface DomaineAnalytiqueService {

    List<DomaineAnalytique> findAll();

    List<DomaineAnalytique> findAllByIds(List<Long> ids);

    List<DomaineAnalytique> findByEspace(Long espaceId);

    List<DomaineAnalytique> findByTbGroup(Long tbGroupId);

    Optional<DomaineAnalytique> findById(Long id);

    Optional<DomaineAnalytique> findBySourceThemeKey(String sourceThemeKey);

    DomaineAnalytique create(DomaineAnalytiqueRequestDto requestDto);

    DomaineAnalytique update(Long id, DomaineAnalytiqueRequestDto requestDto);

    void delete(Long id);

    DomaineAnalytiqueSection createSection(Long domaineAnalytiqueId, DomaineAnalytiqueSectionRequestDto requestDto);

    DomaineAnalytiqueSection updateSection(Long sectionId, DomaineAnalytiqueSectionRequestDto requestDto);

    List<DomaineAnalytiqueSection> findSectionsByDomaineAnalytique(Long domaineAnalytiqueId);

    Optional<DomaineAnalytiqueSection> findSectionById(Long sectionId);

    void reorderSections(Long domaineAnalytiqueId, List<ReorderByIdItem> items);

    void deleteSection(Long sectionId);

    CategorieAnalytique createCategory(CategorieAnalytiqueRequestDto requestDto);

    CategorieAnalytique updateCategory(Long id, CategorieAnalytiqueRequestDto requestDto);

    void reorderCategories(Long domaineAnalytiqueId, List<ReorderByIdItem> items);

    void deleteCategory(Long id);

    CategorieAnalytiqueSection createCategorySection(Long categorieAnalytiqueId,
            CategorieAnalytiqueSectionRequestDto requestDto);

    CategorieAnalytiqueSection updateCategorySection(Long sectionId,
            CategorieAnalytiqueSectionRequestDto requestDto);

    List<CategorieAnalytiqueSection> findSectionsByCategory(Long categorieAnalytiqueId);

    Optional<CategorieAnalytiqueSection> findCategorySectionById(Long sectionId);

    void reorderCategorySections(Long categorieAnalytiqueId, List<ReorderByIdItem> items);

    void deleteCategorySection(Long sectionId);

    List<CategorieAnalytique> findCategoriesByDomaineAnalytique(Long domaineAnalytiqueId);

    Optional<CategorieAnalytique> findCategoryById(Long categoryId);

    EspaceDomaineAnalytique attachToEspace(Long espaceId, DomaineAnalytiqueLinkRequestDto requestDto);

    void reorderEspaceLinks(Long espaceId, List<ReorderByIdItem> items);

    void detachFromEspace(Long espaceId, Long domaineAnalytiqueId);

    List<EspaceDomaineAnalytique> findEspaceLinks(Long espaceId);

    TbGroupDomaineAnalytique attachToTbGroup(Long tbGroupId, DomaineAnalytiqueLinkRequestDto requestDto);

    void reorderTbGroupLinks(Long tbGroupId, List<ReorderByIdItem> items);

    void detachFromTbGroup(Long tbGroupId, Long domaineAnalytiqueId);

    List<TbGroupDomaineAnalytique> findTbGroupLinks(Long tbGroupId);

    PortailAnalytiqueTransitionSummaryDto getTransitionSummary();
}
