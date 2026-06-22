package ma.org.ormt.modules.analytics.domain.services.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytiqueSection;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueSectionRepository;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.dtos.PortailAnalytiqueTransitionSummaryDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueLinkRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.ReorderByIdItem;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytiqueSection;
import ma.org.ormt.modules.analytics.domain.repositories.DomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.domain.repositories.DomaineAnalytiqueSectionRepository;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.repositories.TbGroupRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DomaineAnalytiqueServiceImpl implements DomaineAnalytiqueService {

    private final DomaineAnalytiqueRepository domaineAnalytiqueRepository;
    private final DomaineAnalytiqueSectionRepository domaineAnalytiqueSectionRepository;
    private final CategorieAnalytiqueRepository categorieAnalytiqueRepository;
    private final CategorieAnalytiqueSectionRepository categorieAnalytiqueSectionRepository;
    private final EspaceDomaineAnalytiqueRepository espaceDomaineAnalytiqueRepository;
    private final TbGroupDomaineAnalytiqueRepository tbGroupDomaineAnalytiqueRepository;
    private final EspaceRepository espaceRepository;
    private final TbGroupRepository tbGroupRepository;
    private final TbdDashboardRepository tbdDashboardRepository;
    private final DomaineAnalytiqueNamingService namingService;
    private final DomaineRepository domaineRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DomaineAnalytique> findAll() {
        return domaineAnalytiqueRepository.findAll().stream().map(this::hydrateDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomaineAnalytique> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Map<Long, DomaineAnalytique> byId = new LinkedHashMap<>();
        domaineAnalytiqueRepository.findAllById(ids).forEach(domain -> byId.put(domain.getId(), hydrateDomain(domain)));
        List<DomaineAnalytique> ordered = new ArrayList<>();
        ids.forEach(id -> {
            DomaineAnalytique domain = byId.get(id);
            if (domain != null) {
                ordered.add(domain);
            }
        });
        return ordered;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomaineAnalytique> findByEspace(Long espaceId) {
        return findAllByIds(domaineAnalytiqueRepository.findIdsByEspaceId(espaceId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomaineAnalytique> findByTbGroup(Long tbGroupId) {
        return findAllByIds(domaineAnalytiqueRepository.findIdsByTbGroupId(tbGroupId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomaineAnalytique> findById(Long id) {
        return domaineAnalytiqueRepository.findById(id).map(this::hydrateDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomaineAnalytique> findBySourceThemeKey(String sourceThemeKey) {
        return domaineAnalytiqueRepository.findBySourceThemeKey(sourceThemeKey).map(this::hydrateDomain);
    }

    @Override
    public DomaineAnalytique create(DomaineAnalytiqueRequestDto requestDto) {
        DomaineAnalytique domaineAnalytique = DomaineAnalytique.builder().build();
        applyDomainRequest(domaineAnalytique, requestDto, null);
        return hydrateDomain(domaineAnalytiqueRepository.save(domaineAnalytique));
    }

    @Override
    public DomaineAnalytique update(Long id, DomaineAnalytiqueRequestDto requestDto) {
        DomaineAnalytique domaineAnalytique = domaineAnalytiqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Domaine analytique non trouvé"));
        applyDomainRequest(domaineAnalytique, requestDto, id);
        return hydrateDomain(domaineAnalytiqueRepository.save(domaineAnalytique));
    }

    @Override
    public void delete(Long id) {
        if (!domaineAnalytiqueRepository.existsById(id)) {
            throw new EntityNotFoundException("Domaine analytique non trouvé");
        }
        domaineAnalytiqueRepository.deleteById(id);
    }

    @Override
    public DomaineAnalytiqueSection createSection(Long domaineAnalytiqueId, DomaineAnalytiqueSectionRequestDto requestDto) {
        DomaineAnalytique domain = getDomainOrThrow(domaineAnalytiqueId);
        DomaineAnalytiqueSection section = DomaineAnalytiqueSection.builder()
                .domaineAnalytique(domain)
                .build();
        applyDomainSectionRequest(section, requestDto);
        return domaineAnalytiqueSectionRepository.save(section);
    }

    @Override
    public DomaineAnalytiqueSection updateSection(Long sectionId, DomaineAnalytiqueSectionRequestDto requestDto) {
        DomaineAnalytiqueSection section = domaineAnalytiqueSectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section de domaine analytique non trouvée"));
        applyDomainSectionRequest(section, requestDto);
        return domaineAnalytiqueSectionRepository.save(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomaineAnalytiqueSection> findSectionsByDomaineAnalytique(Long domaineAnalytiqueId) {
        getDomainOrThrow(domaineAnalytiqueId);
        return domaineAnalytiqueSectionRepository.findByDomaineAnalytiqueIdOrderByOrdreAsc(domaineAnalytiqueId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomaineAnalytiqueSection> findSectionById(Long sectionId) {
        return domaineAnalytiqueSectionRepository.findById(sectionId);
    }

    @Override
    public void reorderSections(Long domaineAnalytiqueId, List<ReorderByIdItem> items) {
        getDomainOrThrow(domaineAnalytiqueId);
        List<DomaineAnalytiqueSection> existing = domaineAnalytiqueSectionRepository
                .findByDomaineAnalytiqueIdOrderByOrdreAsc(domaineAnalytiqueId);
        reorderOwnedItems(existing, items, DomaineAnalytiqueSection::getId, DomaineAnalytiqueSection::setOrdre,
                "les sections de ce domaine analytique");
        domaineAnalytiqueSectionRepository.saveAll(existing);
    }

    @Override
    public void deleteSection(Long sectionId) {
        if (!domaineAnalytiqueSectionRepository.existsById(sectionId)) {
            throw new EntityNotFoundException("Section de domaine analytique non trouvée");
        }
        domaineAnalytiqueSectionRepository.deleteById(sectionId);
    }

    @Override
    public CategorieAnalytique createCategory(CategorieAnalytiqueRequestDto requestDto) {
        CategorieAnalytique category = CategorieAnalytique.builder().build();
        applyCategoryRequest(category, requestDto, null);
        return categorieAnalytiqueRepository.save(category);
    }

    @Override
    public CategorieAnalytique updateCategory(Long id, CategorieAnalytiqueRequestDto requestDto) {
        CategorieAnalytique category = categorieAnalytiqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie analytique non trouvée"));
        applyCategoryRequest(category, requestDto, id);
        return categorieAnalytiqueRepository.save(category);
    }

    @Override
    public void reorderCategories(Long domaineAnalytiqueId, List<ReorderByIdItem> items) {
        getDomainOrThrow(domaineAnalytiqueId);
        List<CategorieAnalytique> existing = categorieAnalytiqueRepository
                .findByDomaineAnalytiqueIdOrderByOrdreAscLibelleAsc(domaineAnalytiqueId);
        reorderOwnedItems(existing, items, CategorieAnalytique::getId, CategorieAnalytique::setOrdre,
                "les catégories de ce domaine analytique");
        categorieAnalytiqueRepository.saveAll(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categorieAnalytiqueRepository.existsById(id)) {
            throw new EntityNotFoundException("Catégorie analytique non trouvée");
        }
        categorieAnalytiqueRepository.deleteById(id);
    }

    @Override
    public CategorieAnalytiqueSection createCategorySection(Long categorieAnalytiqueId,
            CategorieAnalytiqueSectionRequestDto requestDto) {
        CategorieAnalytique category = getCategoryOrThrow(categorieAnalytiqueId);
        CategorieAnalytiqueSection section = CategorieAnalytiqueSection.builder()
                .categorieAnalytique(category)
                .build();
        applyCategorySectionRequest(section, requestDto);
        return categorieAnalytiqueSectionRepository.save(section);
    }

    @Override
    public CategorieAnalytiqueSection updateCategorySection(Long sectionId,
            CategorieAnalytiqueSectionRequestDto requestDto) {
        CategorieAnalytiqueSection section = categorieAnalytiqueSectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section de catégorie analytique non trouvée"));
        applyCategorySectionRequest(section, requestDto);
        return categorieAnalytiqueSectionRepository.save(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieAnalytiqueSection> findSectionsByCategory(Long categorieAnalytiqueId) {
        getCategoryOrThrow(categorieAnalytiqueId);
        return categorieAnalytiqueSectionRepository.findByCategorieAnalytiqueIdOrderByOrdreAsc(categorieAnalytiqueId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategorieAnalytiqueSection> findCategorySectionById(Long sectionId) {
        return categorieAnalytiqueSectionRepository.findById(sectionId);
    }

    @Override
    public void reorderCategorySections(Long categorieAnalytiqueId, List<ReorderByIdItem> items) {
        getCategoryOrThrow(categorieAnalytiqueId);
        List<CategorieAnalytiqueSection> existing = categorieAnalytiqueSectionRepository
                .findByCategorieAnalytiqueIdOrderByOrdreAsc(categorieAnalytiqueId);
        reorderOwnedItems(existing, items, CategorieAnalytiqueSection::getId, CategorieAnalytiqueSection::setOrdre,
                "les sections de cette catégorie analytique");
        categorieAnalytiqueSectionRepository.saveAll(existing);
    }

    @Override
    public void deleteCategorySection(Long sectionId) {
        if (!categorieAnalytiqueSectionRepository.existsById(sectionId)) {
            throw new EntityNotFoundException("Section de catégorie analytique non trouvée");
        }
        categorieAnalytiqueSectionRepository.deleteById(sectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieAnalytique> findCategoriesByDomaineAnalytique(Long domaineAnalytiqueId) {
        return categorieAnalytiqueRepository.findByDomaineAnalytiqueIdOrderByOrdreAscLibelleAsc(domaineAnalytiqueId)
                .stream()
                .map(this::hydrateCategory)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategorieAnalytique> findCategoryById(Long categoryId) {
        return categorieAnalytiqueRepository.findById(categoryId).map(this::hydrateCategory);
    }

    @Override
    public EspaceDomaineAnalytique attachToEspace(Long espaceId, DomaineAnalytiqueLinkRequestDto requestDto) {
        Espace espace = espaceRepository.findById(espaceId)
                .orElseThrow(() -> new EntityNotFoundException("Espace non trouvé"));
        DomaineAnalytique domain = getDomainOrThrow(requestDto.getDomaineAnalytiqueId());
        EspaceDomaineAnalytique link = espaceDomaineAnalytiqueRepository
                .findByEspaceIdAndDomaineAnalytiqueId(espaceId, requestDto.getDomaineAnalytiqueId())
                .orElseGet(() -> EspaceDomaineAnalytique.builder().espace(espace).domaineAnalytique(domain).build());
        link.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        return espaceDomaineAnalytiqueRepository.save(link);
    }

    @Override
    public void reorderEspaceLinks(Long espaceId, List<ReorderByIdItem> items) {
        espaceRepository.findById(espaceId).orElseThrow(() -> new EntityNotFoundException("Espace non trouvé"));
        List<EspaceDomaineAnalytique> existing = espaceDomaineAnalytiqueRepository.findByEspaceIdOrderByOrdreAsc(espaceId);
        reorderOwnedItems(existing, items, EspaceDomaineAnalytique::getId, EspaceDomaineAnalytique::setOrdre,
                "les liens de cet espace");
        espaceDomaineAnalytiqueRepository.saveAll(existing);
    }

    @Override
    public void detachFromEspace(Long espaceId, Long domaineAnalytiqueId) {
        EspaceDomaineAnalytique link = espaceDomaineAnalytiqueRepository.findByEspaceIdAndDomaineAnalytiqueId(espaceId,
                domaineAnalytiqueId)
                .orElseThrow(() -> new EntityNotFoundException("Association espace/domaine analytique non trouvée"));
        espaceDomaineAnalytiqueRepository.delete(link);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspaceDomaineAnalytique> findEspaceLinks(Long espaceId) {
        return espaceDomaineAnalytiqueRepository.findByEspaceIdOrderByOrdreAsc(espaceId);
    }

    @Override
    public TbGroupDomaineAnalytique attachToTbGroup(Long tbGroupId, DomaineAnalytiqueLinkRequestDto requestDto) {
        TbGroup tbGroup = tbGroupRepository.findById(tbGroupId)
                .orElseThrow(() -> new EntityNotFoundException("TB group non trouvé"));
        DomaineAnalytique domain = getDomainOrThrow(requestDto.getDomaineAnalytiqueId());
        TbGroupDomaineAnalytique link = tbGroupDomaineAnalytiqueRepository
                .findByTbGroupIdAndDomaineAnalytiqueId(tbGroupId, requestDto.getDomaineAnalytiqueId())
                .orElseGet(() -> TbGroupDomaineAnalytique.builder()
                        .tbGroup(tbGroup)
                        .domaineAnalytique(domain)
                        .build());
        link.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        return tbGroupDomaineAnalytiqueRepository.save(link);
    }

    @Override
    public void reorderTbGroupLinks(Long tbGroupId, List<ReorderByIdItem> items) {
        tbGroupRepository.findById(tbGroupId)
                .orElseThrow(() -> new EntityNotFoundException("TB group non trouvé"));
        List<TbGroupDomaineAnalytique> existing = tbGroupDomaineAnalytiqueRepository
                .findByTbGroupIdOrderByOrdreAsc(tbGroupId);
        reorderOwnedItems(existing, items, TbGroupDomaineAnalytique::getId, TbGroupDomaineAnalytique::setOrdre,
                "les liens de ce tb_group");
        tbGroupDomaineAnalytiqueRepository.saveAll(existing);
    }

    @Override
    public void detachFromTbGroup(Long tbGroupId, Long domaineAnalytiqueId) {
        TbGroupDomaineAnalytique link = tbGroupDomaineAnalytiqueRepository
                .findByTbGroupIdAndDomaineAnalytiqueId(tbGroupId, domaineAnalytiqueId)
                .orElseThrow(() -> new EntityNotFoundException("Association tb_group/domaine analytique non trouvée"));
        tbGroupDomaineAnalytiqueRepository.delete(link);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TbGroupDomaineAnalytique> findTbGroupLinks(Long tbGroupId) {
        return tbGroupDomaineAnalytiqueRepository.findByTbGroupIdOrderByOrdreAsc(tbGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public PortailAnalytiqueTransitionSummaryDto getTransitionSummary() {
        PortailAnalytiqueTransitionSummaryDto dto = new PortailAnalytiqueTransitionSummaryDto();
        List<Domaine> legacyDomaines = domaineRepository.findAll();
        List<DomaineAnalytique> domainesAnalytiques = domaineAnalytiqueRepository.findAll();
        List<CategorieAnalytique> categoriesAnalytiques = categorieAnalytiqueRepository.findAll();
        dto.setLegacyDomaines(legacyDomaines.size());
        dto.setLegacyTbDomaines(0L);
        dto.setLegacyEspacesDomaines(0L);
        dto.setLegacyTbGroupDomaines(0L);
        dto.setLegacyTbdCategories(0L);
        dto.setDomainesAnalytiques(domainesAnalytiques.size());
        dto.setEspaceDomaineAnalytiquesLinks(espaceDomaineAnalytiqueRepository.count());
        dto.setTbGroupDomaineAnalytiquesLinks(tbGroupDomaineAnalytiqueRepository.count());
        dto.setCategoriesAnalytiques(categoriesAnalytiques.size());

        Set<String> analyticsThemeKeys = domainesAnalytiques.stream()
                .flatMap(domain -> java.util.stream.Stream.of(domain.getSourceThemeKey(), domain.getNom()))
                .filter(this::hasText)
                .map(namingService::normalizeThemeKey)
                .filter(this::hasText)
                .collect(Collectors.toSet());

        dto.setLegacyDomainesAvecContenuEditorial(0L);
        dto.setLegacyDomainesSansMappingAnalytique(legacyDomaines.stream()
                .map(Domaine::getNom)
                .map(namingService::normalizeThemeKey)
                .filter(this::hasText)
                .filter(themeKey -> !analyticsThemeKeys.contains(themeKey))
                .count());
        dto.setLegacyTbDomainesSansMappingAnalytique(0L);

        long categoriesAvecTbd = categoriesAnalytiques.stream()
                .filter(category -> category.getTbdDashboard() != null)
                .count();
        dto.setCategoriesAnalytiquesAvecTbd(categoriesAvecTbd);
        dto.setCategoriesAnalytiquesSansTbd(categoriesAnalytiques.size() - categoriesAvecTbd);

        Set<Long> domainIdsWithHeroSection = domaineAnalytiqueSectionRepository.findAll().stream()
                .filter(section -> "hero".equalsIgnoreCase(section.getType()))
                .map(section -> section.getDomaineAnalytique() != null ? section.getDomaineAnalytique().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        dto.setDomainesAnalytiquesAvecHeroSection(domainIdsWithHeroSection.size());

        dto.setDomainesAnalytiquesSansContenuEditorial(domainesAnalytiques.stream()
                .filter(domain -> !hasEditorialContent(domain.getDescription(), domain.getApropos(), domain.getImageUrl()))
                .filter(domain -> !domainIdsWithHeroSection.contains(domain.getId()))
                .count());

        dto.setCategoriesAnalytiquesAvecSectionTbdEmbed(categorieAnalytiqueSectionRepository.findAll().stream()
                .filter(section -> "tbd_embed".equalsIgnoreCase(section.getType()))
                .map(section -> section.getCategorieAnalytique() != null ? section.getCategorieAnalytique().getId() : null)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count());

        Map<Long, String> analyticsDomainThemeById = domainesAnalytiques.stream()
                .filter(domain -> domain.getId() != null)
                .collect(Collectors.toMap(
                        DomaineAnalytique::getId,
                        domain -> normalizedAnalyticsThemeKey(domain),
                        (left, right) -> left,
                        LinkedHashMap::new));
        dto.setLegacyTbdCategoriesSansMappingAnalytique(0L);

        Set<Long> espaceDomainIds = espaceDomaineAnalytiqueRepository.findAll().stream()
                .map(link -> link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> tbGroupDomainIds = tbGroupDomaineAnalytiqueRepository.findAll().stream()
                .map(link -> link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        espaceDomainIds.retainAll(tbGroupDomainIds);
        dto.setDomainesAnalytiquesPartages(espaceDomainIds.size());

        dto.setReadyToDeprecateDomaineEditorialFields(
                dto.getLegacyDomainesSansMappingAnalytique() == 0
                        && (dto.getLegacyDomainesAvecContenuEditorial() == 0
                                || dto.getDomainesAnalytiquesSansContenuEditorial() == 0));
        dto.setReadyToDeprecateTbDomaine(
                dto.getLegacyTbDomainesSansMappingAnalytique() == 0
                        && dto.getTbGroupDomaineAnalytiquesLinks() >= 0);
        dto.setReadyToDeprecateTbdCategorie(
                dto.getLegacyTbdCategoriesSansMappingAnalytique() == 0
                        && dto.getLegacyTbdCategories() <= dto.getCategoriesAnalytiques());
        dto.setReadyToDeprecateLegacyPortalTaxonomy(
                dto.isReadyToDeprecateTbDomaine() && dto.isReadyToDeprecateTbdCategorie());
        return dto;
    }

    private void applyDomainRequest(DomaineAnalytique domaineAnalytique, DomaineAnalytiqueRequestDto requestDto, Long currentId) {
        String nom = requestDto.getNom().trim().toLowerCase();
        String slug = requestDto.getSlug() == null || requestDto.getSlug().isBlank()
                ? namingService.normalizeSlug(requestDto.getTitre())
                : namingService.normalizeSlug(requestDto.getSlug());
        domaineAnalytiqueRepository.findByNom(nom)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Un domaine analytique avec ce nom existe déjà.");
                });
        domaineAnalytiqueRepository.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Un domaine analytique avec ce slug existe déjà.");
                });
        domaineAnalytique.setNom(nom);
        domaineAnalytique.setTitre(requestDto.getTitre().trim());
        domaineAnalytique.setDescription(requestDto.getDescription());
        domaineAnalytique.setApropos(requestDto.getApropos());
        domaineAnalytique.setImageUrl(requestDto.getImageUrl());
        domaineAnalytique.setSlug(slug);
        domaineAnalytique.setSourceThemeKey(requestDto.getSourceThemeKey());
        domaineAnalytique.setMetadataJson(requestDto.getMetadataJson());
        domaineAnalytique.setActif(requestDto.getActif());
    }

    private String normalizedAnalyticsThemeKey(DomaineAnalytique domain) {
        if (domain == null) {
            return "";
        }
        String themeKey = hasText(domain.getSourceThemeKey()) ? domain.getSourceThemeKey() : domain.getNom();
        return namingService.normalizeThemeKey(themeKey);
    }

    private String buildCategoryMappingKey(String themeKey, String categoryKey) {
        String normalizedThemeKey = namingService.normalizeThemeKey(themeKey);
        String normalizedCategoryKey = namingService.normalizeSlug(categoryKey);
        if (!hasText(normalizedThemeKey) || !hasText(normalizedCategoryKey)) {
            return "";
        }
        return normalizedThemeKey + "::" + normalizedCategoryKey;
    }

    private boolean hasEditorialContent(String description, String apropos, String imageUrl) {
        return hasText(description) || hasText(apropos) || hasText(imageUrl);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void applyDomainSectionRequest(DomaineAnalytiqueSection section, DomaineAnalytiqueSectionRequestDto requestDto) {
        section.setType(requestDto.getType().trim());
        section.setTitre(requestDto.getTitre());
        section.setContentJson(requestDto.getContentJson());
        section.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        section.setActif(requestDto.getActif());
    }

    private void applyCategoryRequest(CategorieAnalytique category, CategorieAnalytiqueRequestDto requestDto, Long currentId) {
        DomaineAnalytique domain = getDomainOrThrow(requestDto.getDomaineAnalytiqueId());
        String nom = requestDto.getNom().trim().toLowerCase();
        String slug = requestDto.getSlug() == null || requestDto.getSlug().isBlank()
                ? namingService.normalizeSlug(requestDto.getLibelle())
                : namingService.normalizeSlug(requestDto.getSlug());
        categorieAnalytiqueRepository.findByDomaineAnalytiqueIdAndNom(domain.getId(), nom)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Une catégorie analytique avec ce nom existe déjà.");
                });
        TbdDashboard tbd = null;
        if (requestDto.getTbdDashboardId() != null) {
            tbd = tbdDashboardRepository.findById(requestDto.getTbdDashboardId())
                    .orElseThrow(() -> new EntityNotFoundException("TBD principal non trouvé"));
        }
        category.setDomaineAnalytique(domain);
        category.setNom(nom);
        category.setLibelle(requestDto.getLibelle().trim());
        category.setDescription(requestDto.getDescription());
        category.setSlug(slug);
        category.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        category.setTbdDashboard(tbd);
        category.setActif(requestDto.getActif());
    }

    private void applyCategorySectionRequest(CategorieAnalytiqueSection section,
            CategorieAnalytiqueSectionRequestDto requestDto) {
        section.setType(requestDto.getType().trim());
        section.setTitre(requestDto.getTitre());
        section.setContentJson(requestDto.getContentJson());
        section.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        section.setActif(requestDto.getActif());
    }

    private DomaineAnalytique getDomainOrThrow(Long id) {
        return domaineAnalytiqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Domaine analytique non trouvé"));
    }

    private CategorieAnalytique getCategoryOrThrow(Long id) {
        return categorieAnalytiqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie analytique non trouvée"));
    }

    private <T> void reorderOwnedItems(List<T> existing, List<ReorderByIdItem> items,
            java.util.function.Function<T, Long> idExtractor,
            java.util.function.BiConsumer<T, Integer> ordreSetter,
            String targetLabel) {
        if (existing.isEmpty()) {
            return;
        }
        java.util.Map<Long, T> byId = existing.stream()
                .collect(java.util.stream.Collectors.toMap(idExtractor, item -> item));
        java.util.Set<Long> currentIds = byId.keySet();
        java.util.Set<Long> requestedIds = items.stream().map(ReorderByIdItem::getId)
                .collect(java.util.stream.Collectors.toSet());
        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException("Le reorder doit inclure tous et seulement " + targetLabel + ".");
        }
        int n = items.size();
        boolean withinRange = items.stream().allMatch(i -> i.getOrdre() != null && i.getOrdre() >= 0 && i.getOrdre() < n);
        if (!withinRange) {
            throw new IllegalArgumentException("Les ordres doivent être compris entre 0 et " + (n - 1) + ".");
        }
        java.util.HashSet<Integer> ordreSet = new java.util.HashSet<>();
        for (ReorderByIdItem item : items) {
            if (!ordreSet.add(item.getOrdre())) {
                throw new IllegalArgumentException("Les ordres du reorder doivent être uniques.");
            }
        }
        java.util.Map<Long, Integer> newOrdreById = items.stream()
                .collect(java.util.stream.Collectors.toMap(ReorderByIdItem::getId, ReorderByIdItem::getOrdre));
        for (T existingItem : existing) {
            Integer newOrdre = newOrdreById.get(idExtractor.apply(existingItem));
            ordreSetter.accept(existingItem, newOrdre);
        }
    }

    private DomaineAnalytique hydrateDomain(DomaineAnalytique domain) {
        syncCollection(domain.getSections(),
                domaineAnalytiqueSectionRepository.findByDomaineAnalytiqueIdOrderByOrdreAsc(domain.getId()));
        syncCollection(domain.getCategories(), findCategoriesByDomaineAnalytique(domain.getId()));
        return domain;
    }

    private CategorieAnalytique hydrateCategory(CategorieAnalytique category) {
        syncCollection(category.getSections(),
                categorieAnalytiqueSectionRepository.findByCategorieAnalytiqueIdOrderByOrdreAsc(category.getId()));
        return category;
    }

    private <T> void syncCollection(List<T> target, List<T> source) {
        if (target == null) {
            return;
        }
        target.clear();
        if (source != null && !source.isEmpty()) {
            target.addAll(source);
        }
    }
}
