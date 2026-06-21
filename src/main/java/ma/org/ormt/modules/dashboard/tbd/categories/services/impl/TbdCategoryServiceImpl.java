package ma.org.ormt.modules.dashboard.tbd.categories.services.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.repository.TBDomaineIndicateurRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.repositories.TBDomaineRepository;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.request.TbdCategoryRequestDto;
import ma.org.ormt.modules.dashboard.tbd.categories.models.TbdCategory;
import ma.org.ormt.modules.dashboard.tbd.categories.repositories.TbdCategoryRepository;
import ma.org.ormt.modules.dashboard.tbd.categories.services.TbdCategoryService;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdAssignationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TbdCategoryServiceImpl implements TbdCategoryService {

    private final TbdCategoryRepository categoryRepository;
    private final TBDomaineIndicateurRepository tbDomaineIndicateurRepository;
    private final TBDomaineRepository tbDomaineRepository;
    private final TbdAssignationRepository tbdAssignationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TbdCategory> findActiveCategories() {
        return categoryRepository.findByActifTrueOrderByTbDomaineLibelleAscOrdreAscLibelleAsc();
    }

    @Override
    public void syncCategoriesFromDomaines() {
        List<TBDomaineIndicateur> associations = tbDomaineIndicateurRepository
                .findCategorizedByDomaineOrderByDomaineAndOrdre();
        Map<String, TBDomaineIndicateur> categoriesByKey = new LinkedHashMap<>();
        for (TBDomaineIndicateur association : associations) {
            String categorie = association.getCategorie() != null ? association.getCategorie().trim() : "";
            if (association.getTbDomaine() == null || categorie.isBlank()) {
                continue;
            }
            String key = association.getTbDomaine().getId() + "::" + categorie.toLowerCase(Locale.ROOT);
            categoriesByKey.putIfAbsent(key, association);
        }
        for (TBDomaineIndicateur association : categoriesByKey.values()) {
            String libelle = association.getCategorie().trim();
            String nom = buildCategoryNom(association.getTbDomaine().getId(), libelle);
            TbdCategory category = categoryRepository
                    .findByTbDomaineAndNom(association.getTbDomaine(), nom)
                    .orElseGet(TbdCategory::new);
            category.setNom(nom);
            category.setLibelle(libelle);
            category.setTbDomaine(association.getTbDomaine());
            category.setOrdre(association.getOrdre() != null ? association.getOrdre() : 0);
            category.setActif(true);
            categoryRepository.save(category);
        }
    }

    @Override
    public TbdCategory createCategory(TbdCategoryRequestDto requestDto) {
        TbdCategory category = new TbdCategory();
        applyCategoryRequest(category, requestDto);
        return categoryRepository.save(category);
    }

    @Override
    public TbdCategory updateCategory(Long id, TbdCategoryRequestDto requestDto) {
        TbdCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie TBD non trouvée"));
        applyCategoryRequest(category, requestDto);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        TbdCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie TBD non trouvée"));
        boolean isAssignedToDynamicDashboard = tbdAssignationRepository
                .findByCibleTypeAndCibleId("CATEGORIE", id)
                .isPresent();
        if (isAssignedToDynamicDashboard) {
            throw new IllegalStateException(
                    "Impossible de supprimer une catégorie déjà assignée à un dashboard dynamique.");
        }
        categoryRepository.delete(category);
    }

    private void applyCategoryRequest(TbdCategory category, TbdCategoryRequestDto requestDto) {
        TBDomaine tbDomaine = tbDomaineRepository.findById(requestDto.getTbDomaineId())
                .orElseThrow(() -> new EntityNotFoundException("Domaine TB non trouvé"));
        String libelle = requestDto.getLibelle().trim();
        String nom = buildCategoryNom(tbDomaine.getId(), libelle);
        categoryRepository.findByTbDomaineAndNom(tbDomaine, nom)
                .filter(existing -> !existing.getId().equals(category.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Cette catégorie existe déjà dans ce domaine.");
                });
        category.setTbDomaine(tbDomaine);
        category.setLibelle(libelle);
        category.setNom(nom);
        category.setDescription(requestDto.getDescription());
        category.setOrdre(requestDto.getOrdre() != null ? requestDto.getOrdre() : 0);
        category.setActif(requestDto.getActif() != null ? requestDto.getActif() : true);
    }

    private String buildCategoryNom(Long tbDomaineId, String libelle) {
        String slug = libelle.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return "tbd-" + tbDomaineId + "-" + slug;
    }
}
