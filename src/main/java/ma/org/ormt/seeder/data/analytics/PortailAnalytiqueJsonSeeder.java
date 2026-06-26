package ma.org.ormt.seeder.data.analytics;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.utilities.files.FileToMultipartFileConverter;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.repositories.DomaineAnalytiqueRepository;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.repositories.TbGroupRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;

@Log4j2
@Component
@Order(9)
@RequiredArgsConstructor
public class PortailAnalytiqueJsonSeeder implements CommandLineRunner {

    private static final String PORTAIL_ANALYTIQUE_JSON_FILE = "portail_analytique.json";

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final FileDataService fileDataService;
    private final DomaineAnalytiqueRepository domaineAnalytiqueRepository;
    private final CategorieAnalytiqueRepository categorieAnalytiqueRepository;
    private final EspaceDomaineAnalytiqueRepository espaceDomaineAnalytiqueRepository;
    private final TbGroupDomaineAnalytiqueRepository tbGroupDomaineAnalytiqueRepository;
    private final EspaceRepository espaceRepository;
    private final TbGroupRepository tbGroupRepository;
    private final TbdDashboardRepository tbdDashboardRepository;
    private final DomaineAnalytiqueNamingService namingService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;
    private final MinioService minioService;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping portail analytique JSON seeding.");
            return;
        }

        try {
            Path resourcePath = Paths.get(dataExternalPath, "init-data", "analytics");
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping portail analytique JSON seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), PORTAIL_ANALYTIQUE_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("Portail analytique JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            seedFromJson(jsonFile);
            log.info("Portail analytique JSON seeding completed successfully.");
        } catch (Exception exception) {
            log.error("Error during portail analytique JSON seeding: {}", exception.getMessage(), exception);
        }
    }

    @Transactional
    protected void seedFromJson(File jsonFile) {
        try {
            PortailAnalytiqueSeed seed = fileDataService.readJsonFile(jsonFile, PortailAnalytiqueSeed.class);
            if (seed == null) {
                log.warn("No portail analytique payload found in file: {}", jsonFile.getName());
                return;
            }

            upsertDomaines(seed.getDomainesAnalytiques(), seed.getVersion());
            upsertCategories(seed.getCategoriesAnalytiques(), seed.getVersion());
            upsertEspaceLinks(seed.getEspaces());
            upsertTbGroupLinks(seed.getTbGroups());
        } catch (Exception exception) {
            log.error("Error processing portail analytique file {}: {}", jsonFile.getName(), exception.getMessage(),
                    exception);
        }
    }

    private void upsertDomaines(List<DomaineAnalytiqueSeed> domaines, Integer version) {
        if (domaines == null || domaines.isEmpty()) {
            log.warn("No domaines analytiques found in portail analytique JSON.");
            return;
        }

        for (DomaineAnalytiqueSeed seed : domaines) {
            if (seed == null || !hasText(seed.getTitre())) {
                continue;
            }

            String sourceThemeKey = normalizeThemeKey(seed.getSourceThemeKey(), seed.getNom(), seed.getSlug(),
                    seed.getTitre());
            String slug = normalizeSlug(seed.getSlug(), seed.getNom(), seed.getTitre(), sourceThemeKey);
            String nom = normalizeSlug(seed.getNom(), seed.getSlug(), seed.getTitre(), sourceThemeKey);

            Optional<DomaineAnalytique> existing = domaineAnalytiqueRepository.findBySourceThemeKey(sourceThemeKey);
            if (existing.isEmpty()) {
                existing = domaineAnalytiqueRepository.findBySlug(slug);
            }

            DomaineAnalytique domaineAnalytique = existing.orElseGet(() -> DomaineAnalytique.builder().build());
            domaineAnalytique.setNom(nom);
            domaineAnalytique.setTitre(seed.getTitre().trim());
            domaineAnalytique.setDescription(seed.getDescription());
            domaineAnalytique.setApropos(seed.getApropos());
            domaineAnalytique.setImageUrl(resolveImageUrl(seed));
            domaineAnalytique.setSlug(slug);
            domaineAnalytique.setSourceThemeKey(sourceThemeKey);
            domaineAnalytique.setActif(seed.getActif() == null ? true : seed.getActif());
            domaineAnalytique.setMetadataJson(analyticsSeedJsonBuilder.metadata("portail_analytique_json", Map.of(
                    "version", version == null ? 1 : version,
                    "sourceThemeKey", sourceThemeKey,
                    "slug", slug)));

            domaineAnalytiqueRepository.save(domaineAnalytique);
        }
    }

    private void upsertCategories(List<CategorieAnalytiqueSeed> categories, Integer version) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        for (CategorieAnalytiqueSeed seed : categories) {
            if (seed == null || !hasText(seed.getDomaineSlug()) || !hasText(seed.getLibelle())) {
                continue;
            }

            String domaineSlug = namingService.normalizeSlug(seed.getDomaineSlug());
            DomaineAnalytique domaineAnalytique = domaineAnalytiqueRepository.findBySlug(domaineSlug).orElse(null);
            if (domaineAnalytique == null) {
                log.warn("Skipping catégorie analytique '{}' because domaine '{}' was not found.", seed.getLibelle(),
                        seed.getDomaineSlug());
                continue;
            }

            String nom = normalizeSlug(seed.getNom(), seed.getSlug(), seed.getLibelle());
            String slug = normalizeSlug(seed.getSlug(), seed.getNom(), seed.getLibelle());

            CategorieAnalytique categorieAnalytique = categorieAnalytiqueRepository
                    .findByDomaineAnalytiqueIdAndNom(domaineAnalytique.getId(), nom)
                    .orElseGet(() -> CategorieAnalytique.builder().build());

            categorieAnalytique.setDomaineAnalytique(domaineAnalytique);
            categorieAnalytique.setNom(nom);
            categorieAnalytique.setLibelle(seed.getLibelle().trim());
            categorieAnalytique.setDescription(seed.getDescription());
            categorieAnalytique.setSlug(slug);
            categorieAnalytique.setOrdre(seed.getOrdre() == null ? 0 : seed.getOrdre());
            categorieAnalytique.setActif(seed.getActif() == null ? true : seed.getActif());
            categorieAnalytique.setTbdDashboard(resolveTbd(seed.getTbdNom()));

            categorieAnalytiqueRepository.save(categorieAnalytique);
        }
    }

    private void upsertEspaceLinks(List<EspaceSeed> espaces) {
        if (espaces == null || espaces.isEmpty()) {
            return;
        }

        for (EspaceSeed espaceSeed : espaces) {
            if (espaceSeed == null || !hasText(espaceSeed.getEspaceNom())) {
                continue;
            }

            Espace espace = espaceRepository.findByNom(espaceSeed.getEspaceNom()).orElse(null);
            if (espace == null) {
                log.warn("Skipping espace linkage because espace '{}' was not found.", espaceSeed.getEspaceNom());
                continue;
            }

            List<String> domaines = espaceSeed.getDomaines();
            if (domaines == null || domaines.isEmpty()) {
                continue;
            }

            for (int index = 0; index < domaines.size(); index++) {
                String domaineSlug = namingService.normalizeSlug(domaines.get(index));
                DomaineAnalytique domaineAnalytique = domaineAnalytiqueRepository.findBySlug(domaineSlug).orElse(null);
                if (domaineAnalytique == null) {
                    log.warn("Skipping espace '{}' -> domaine '{}' because domain was not found.",
                            espaceSeed.getEspaceNom(), domaines.get(index));
                    continue;
                }

                EspaceDomaineAnalytique link = espaceDomaineAnalytiqueRepository
                        .findByEspaceIdAndDomaineAnalytiqueId(espace.getId(), domaineAnalytique.getId())
                        .orElseGet(() -> EspaceDomaineAnalytique.builder()
                                .espace(espace)
                                .domaineAnalytique(domaineAnalytique)
                                .build());
                link.setOrdre(index);
                espaceDomaineAnalytiqueRepository.save(link);
            }
        }
    }

    private void upsertTbGroupLinks(List<TbGroupSeed> tbGroups) {
        if (tbGroups == null || tbGroups.isEmpty()) {
            return;
        }

        for (TbGroupSeed tbGroupSeed : tbGroups) {
            if (tbGroupSeed == null || !hasText(tbGroupSeed.getTbGroupNom())) {
                continue;
            }

            TbGroup tbGroup = tbGroupRepository.findByNom(tbGroupSeed.getTbGroupNom()).orElse(null);
            if (tbGroup == null) {
                log.warn("Skipping tb_group linkage because tb_group '{}' was not found.", tbGroupSeed.getTbGroupNom());
                continue;
            }

            List<TbGroupDomaineSeed> domaines = tbGroupSeed.getDomaines();
            if (domaines == null || domaines.isEmpty()) {
                continue;
            }

            for (int index = 0; index < domaines.size(); index++) {
                TbGroupDomaineSeed domaineSeed = domaines.get(index);
                if (domaineSeed == null || !hasText(domaineSeed.getSlug())) {
                    continue;
                }

                DomaineAnalytique domaineAnalytique = domaineAnalytiqueRepository
                        .findBySlug(namingService.normalizeSlug(domaineSeed.getSlug()))
                        .orElse(null);
                if (domaineAnalytique == null) {
                    log.warn("Skipping tb_group '{}' -> domaine '{}' because domain was not found.",
                            tbGroupSeed.getTbGroupNom(), domaineSeed.getSlug());
                    continue;
                }

                TbGroupDomaineAnalytique link = tbGroupDomaineAnalytiqueRepository
                        .findByTbGroupIdAndDomaineAnalytiqueId(tbGroup.getId(), domaineAnalytique.getId())
                        .orElseGet(() -> TbGroupDomaineAnalytique.builder()
                                .tbGroup(tbGroup)
                                .domaineAnalytique(domaineAnalytique)
                                .build());
                link.setOrdre(domaineSeed.getOrdre() == null ? index : domaineSeed.getOrdre());
                tbGroupDomaineAnalytiqueRepository.save(link);
            }
        }
    }

    private TbdDashboard resolveTbd(String tbdNom) {
        if (!hasText(tbdNom)) {
            return null;
        }
        return tbdDashboardRepository.findByNomIgnoreCaseAndActifTrue(tbdNom.trim())
                .or(() -> tbdDashboardRepository.findByTitreIgnoreCaseAndActifTrue(tbdNom.trim()))
                .orElseGet(() -> {
                    log.warn("TBD '{}' not found for catégorie analytique.", tbdNom);
                    return null;
                });
    }

    private String resolveImageUrl(DomaineAnalytiqueSeed seed) {
        if (!hasText(seed.getImageUrl())) {
            return null;
        }

        String relativeImagePath = seed.getImageUrl().startsWith("/")
                ? seed.getImageUrl().substring(1)
                : seed.getImageUrl();
        Path imagePath = Paths.get(dataExternalPath, "init-data", "analytics", relativeImagePath);
        if (!Files.exists(imagePath)) {
            log.warn("Image file '{}' not found for domaine analytique '{}'.", seed.getImageUrl(), seed.getTitre());
            return extractFileName(seed.getImageUrl());
        }

        try {
            MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());
            return minioService.uploadFile(imageFile);
        } catch (Exception exception) {
            log.warn("Unable to upload image '{}' for domaine analytique '{}': {}", imagePath, seed.getTitre(),
                    exception.getMessage());
            return extractFileName(seed.getImageUrl());
        }
    }

    private String extractFileName(String value) {
        if (!hasText(value)) {
            return null;
        }
        String normalized = value.replace("\\", "/");
        int separatorIndex = normalized.lastIndexOf('/');
        return separatorIndex >= 0 ? normalized.substring(separatorIndex + 1) : normalized;
    }

    private String normalizeThemeKey(String... values) {
        for (String value : values) {
            String normalized = namingService.normalizeThemeKey(value);
            if (hasText(normalized)) {
                return normalized;
            }
        }
        return "";
    }

    private String normalizeSlug(String... values) {
        for (String value : values) {
            String normalized = namingService.normalizeSlug(value);
            if (hasText(normalized)) {
                return normalized;
            }
        }
        return "";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @Data
    private static class PortailAnalytiqueSeed {
        private Integer version;
        private String territoire;
        private List<DomaineAnalytiqueSeed> domainesAnalytiques;
        private List<CategorieAnalytiqueSeed> categoriesAnalytiques;
        private List<EspaceSeed> espaces;
        private List<TbGroupSeed> tbGroups;
    }

    @Data
    private static class DomaineAnalytiqueSeed {
        private String slug;
        private String nom;
        private String titre;
        private String description;
        private String apropos;
        private String imageUrl;
        private Boolean actif;
        private String sourceThemeKey;
    }

    @Data
    private static class CategorieAnalytiqueSeed {
        private String domaineSlug;
        private String slug;
        private String nom;
        private String libelle;
        private String description;
        private Integer ordre;
        private Boolean actif;
        private String tbdNom;
    }

    @Data
    private static class EspaceSeed {
        private String espaceNom;
        private List<String> domaines;
    }

    @Data
    private static class TbGroupSeed {
        private String tbGroupNom;
        private List<TbGroupDomaineSeed> domaines;
    }

    @Data
    private static class TbGroupDomaineSeed {
        private String slug;
        private Integer ordre;
    }
}
