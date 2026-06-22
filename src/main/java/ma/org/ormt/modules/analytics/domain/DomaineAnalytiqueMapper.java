package ma.org.ormt.modules.analytics.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.espace.dtos.EspaceDomaineAnalytiqueLinkDto;
import ma.org.ormt.modules.analytics.association.tbgroup.TableauBordDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.dtos.TableauBordDomaineAnalytiqueLinkDto;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueSectionDto;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytiqueSection;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueDto;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueSectionDto;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytiqueSection;
import ma.org.ormt.modules.analytics.shared.AnalyticsMapperSupport;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardSummaryDto;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;

@Component
public class DomaineAnalytiqueMapper {

    public List<DomaineAnalytiqueDto> toDtos(List<DomaineAnalytique> domains) {
        return domains.stream().map(domain -> toDto(domain, Collections.emptyList())).toList();
    }

    public DomaineAnalytiqueDto toDto(DomaineAnalytique domain, List<RoleAccesSummaryDto> roleAcces) {
        if (domain == null) {
            return null;
        }
        DomaineAnalytiqueDto dto = new DomaineAnalytiqueDto();
        AnalyticsMapperSupport.copyBase(domain, dto);
        dto.setNom(domain.getNom());
        dto.setTitre(domain.getTitre());
        dto.setDescription(domain.getDescription());
        dto.setApropos(domain.getApropos());
        dto.setImageUrl(domain.getImageUrl());
        dto.setSlug(domain.getSlug());
        dto.setSourceThemeKey(domain.getSourceThemeKey());
        dto.setMetadataJson(domain.getMetadataJson());
        dto.setActif(domain.getActif());
        dto.setSections(toDomainSectionDtos(domain.getSections()));
        dto.setCategories(toCategoryDtos(domain.getCategories()));
        dto.setRoleAcces(roleAcces);
        return dto;
    }

    public DomaineAnalytiqueSectionDto toDomainSectionDto(DomaineAnalytiqueSection section) {
        if (section == null) {
            return null;
        }
        DomaineAnalytiqueSectionDto dto = new DomaineAnalytiqueSectionDto();
        AnalyticsMapperSupport.copyBase(section, dto);
        dto.setType(section.getType());
        dto.setTitre(section.getTitre());
        dto.setContentJson(section.getContentJson());
        dto.setOrdre(section.getOrdre());
        dto.setActif(section.getActif());
        return dto;
    }

    public CategorieAnalytiqueDto toCategoryDto(CategorieAnalytique category) {
        if (category == null) {
            return null;
        }
        CategorieAnalytiqueDto dto = new CategorieAnalytiqueDto();
        AnalyticsMapperSupport.copyBase(category, dto);
        dto.setDomaineAnalytiqueId(category.getDomaineAnalytique() != null ? category.getDomaineAnalytique().getId() : null);
        dto.setNom(category.getNom());
        dto.setLibelle(category.getLibelle());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setOrdre(category.getOrdre());
        dto.setActif(category.getActif());
        dto.setTbdDashboardId(category.getTbdDashboard() != null ? category.getTbdDashboard().getId() : null);
        dto.setTbdPrincipal(toTbdSummary(category.getTbdDashboard()));
        dto.setSections(toCategorySectionDtos(category.getSections()));
        return dto;
    }

    public CategorieAnalytiqueSectionDto toCategorySectionDto(CategorieAnalytiqueSection section) {
        if (section == null) {
            return null;
        }
        CategorieAnalytiqueSectionDto dto = new CategorieAnalytiqueSectionDto();
        AnalyticsMapperSupport.copyBase(section, dto);
        dto.setType(section.getType());
        dto.setTitre(section.getTitre());
        dto.setContentJson(section.getContentJson());
        dto.setOrdre(section.getOrdre());
        dto.setActif(section.getActif());
        return dto;
    }

    public EspaceDomaineAnalytiqueLinkDto toEspaceLinkDto(EspaceDomaineAnalytique link) {
        if (link == null) {
            return null;
        }
        EspaceDomaineAnalytiqueLinkDto dto = new EspaceDomaineAnalytiqueLinkDto();
        dto.setId(link.getId());
        dto.setOrdre(link.getOrdre());
        dto.setEspaceId(link.getEspace() != null ? link.getEspace().getId() : null);
        dto.setEspaceNom(link.getEspace() != null ? link.getEspace().getNom() : null);
        dto.setDomaineAnalytiqueId(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getId() : null);
        dto.setDomaineAnalytiqueNom(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getNom() : null);
        dto.setDomaineAnalytiqueTitre(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getTitre() : null);
        dto.setDomaineAnalytiqueSlug(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getSlug() : null);
        dto.setDomaineAnalytiqueActif(
                link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getActif() : null);
        return dto;
    }

    public TableauBordDomaineAnalytiqueLinkDto toTbGroupLinkDto(TableauBordDomaineAnalytique link) {
        if (link == null) {
            return null;
        }
        TableauBordDomaineAnalytiqueLinkDto dto = new TableauBordDomaineAnalytiqueLinkDto();
        dto.setId(link.getId());
        dto.setOrdre(link.getOrdre());
        dto.setTbGroupId(link.getTableauBord() != null ? link.getTableauBord().getId() : null);
        dto.setTbGroupNom(link.getTableauBord() != null ? link.getTableauBord().getNom() : null);
        dto.setDomaineAnalytiqueId(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getId() : null);
        dto.setDomaineAnalytiqueNom(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getNom() : null);
        dto.setDomaineAnalytiqueTitre(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getTitre() : null);
        dto.setDomaineAnalytiqueSlug(link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getSlug() : null);
        dto.setDomaineAnalytiqueActif(
                link.getDomaineAnalytique() != null ? link.getDomaineAnalytique().getActif() : null);
        return dto;
    }

    private List<DomaineAnalytiqueSectionDto> toDomainSectionDtos(List<DomaineAnalytiqueSection> sections) {
        if (sections == null) {
            return Collections.emptyList();
        }
        return sections.stream().map(this::toDomainSectionDto).toList();
    }

    private List<CategorieAnalytiqueDto> toCategoryDtos(List<CategorieAnalytique> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }
        return categories.stream().map(this::toCategoryDto).toList();
    }

    private List<CategorieAnalytiqueSectionDto> toCategorySectionDtos(List<CategorieAnalytiqueSection> sections) {
        if (sections == null) {
            return Collections.emptyList();
        }
        return sections.stream().map(this::toCategorySectionDto).toList();
    }

    private TbdDashboardSummaryDto toTbdSummary(TbdDashboard dashboard) {
        if (dashboard == null) {
            return null;
        }
        TbdDashboardSummaryDto dto = new TbdDashboardSummaryDto();
        dto.setId(dashboard.getId());
        dto.setNom(dashboard.getNom());
        dto.setTitre(dashboard.getTitre());
        dto.setSousTitre(dashboard.getSousTitre());
        dto.setStatus(dashboard.getStatus());
        dto.setActif(dashboard.getActif());
        dto.setLastModifiedDate(dashboard.getLastModifiedDate());
        return dto;
    }
}
