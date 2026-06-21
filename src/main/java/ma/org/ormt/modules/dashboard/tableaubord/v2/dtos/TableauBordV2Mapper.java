package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos;

import java.util.List;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Categorie;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Widget;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2WidgetItem;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Component
public class TableauBordV2Mapper {

    public TableauBordV2Dto mapToDto(TableauBordV2 entity) {
        return mapToDto(entity, null);
    }

    public TableauBordV2Dto mapToDto(TableauBordV2 entity, RoleAccesService roleAccesService) {
        if (entity == null) {
            return null;
        }
        TableauBordV2Dto dto = new TableauBordV2Dto();
        copyBase(entity, dto);
        dto.setNom(entity.getNom());
        dto.setTitre(entity.getTitre());
        dto.setSousTitre(entity.getSousTitre());
        dto.setDescription(entity.getDescription());
        dto.setSource(entity.getSource());
        dto.setActif(entity.getActif());
        dto.setStatus(entity.getStatus());
        dto.setCategorieId(entity.getCategorie() != null ? entity.getCategorie().getId() : null);
        dto.setCategorie(mapCategorie(entity.getCategorie()));
        dto.setRoleAcces(RoleAccesMappingUtil.mapForRessource(roleAccesService, "tableauBordV2", entity.getId()));
        dto.setThemeJson(entity.getThemeJson());
        dto.setSettingsJson(entity.getSettingsJson());
        if (entity.getWidgets() != null) {
            dto.setWidgets(entity.getWidgets().stream()
                    .map(this::mapToDto)
                    .toList());
        }
        return dto;
    }

    public TableauBordV2Dto mapToDtoSummary(TableauBordV2 entity) {
        if (entity == null) {
            return null;
        }
        TableauBordV2Dto dto = new TableauBordV2Dto();
        copyBase(entity, dto);
        dto.setNom(entity.getNom());
        dto.setTitre(entity.getTitre());
        dto.setSousTitre(entity.getSousTitre());
        dto.setDescription(entity.getDescription());
        dto.setActif(entity.getActif());
        dto.setStatus(entity.getStatus());
        dto.setCategorieId(entity.getCategorie() != null ? entity.getCategorie().getId() : null);
        dto.setCategorie(mapCategorie(entity.getCategorie()));
        return dto;
    }

    public TableauBordV2WidgetDto mapToDto(TableauBordV2Widget entity) {
        if (entity == null) {
            return null;
        }
        TableauBordV2WidgetDto dto = new TableauBordV2WidgetDto();
        copyBase(entity, dto);
        dto.setType(entity.getType());
        dto.setTitre(entity.getTitre());
        dto.setSousTitre(entity.getSousTitre());
        dto.setDescription(entity.getDescription());
        dto.setOrdre(entity.getOrdre());
        dto.setSection(entity.getSection());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        dto.setW(entity.getW());
        dto.setH(entity.getH());
        dto.setConfigJson(entity.getConfigJson());
        dto.setStyleJson(entity.getStyleJson());
        dto.setDataSourceType(entity.getDataSourceType());
        dto.setActif(entity.getActif());
        dto.setIndicateurId(entity.getIndicateur() != null ? entity.getIndicateur().getId() : null);
        dto.setGrapheConfigurationId(
                entity.getGrapheConfiguration() != null ? entity.getGrapheConfiguration().getId() : null);
        dto.setChiffreCleId(entity.getChiffreCle() != null ? entity.getChiffreCle().getId() : null);
        dto.setIndicateur(mapIndicateur(entity.getIndicateur()));
        dto.setGrapheConfiguration(mapGrapheConfiguration(entity.getGrapheConfiguration()));
        dto.setChiffreCle(mapChiffreCle(entity.getChiffreCle()));
        if (entity.getItems() != null) {
            dto.setItems(entity.getItems().stream()
                    .map(this::mapToDto)
                    .toList());
        }
        return dto;
    }

    private TableauBordV2WidgetItemDto mapToDto(TableauBordV2WidgetItem entity) {
        TableauBordV2WidgetItemDto dto = new TableauBordV2WidgetItemDto();
        copyBase(entity, dto);
        dto.setLibelle(entity.getLibelle());
        dto.setValeur(entity.getValeur());
        dto.setUnite(entity.getUnite());
        dto.setDescription(entity.getDescription());
        dto.setOrdre(entity.getOrdre());
        dto.setConfigJson(entity.getConfigJson());
        dto.setStyleJson(entity.getStyleJson());
        dto.setActif(entity.getActif());
        return dto;
    }

    public List<TableauBordV2Dto> mapToDtos(List<TableauBordV2> entities) {
        return entities.stream().map(this::mapToDto).toList();
    }

    public List<TableauBordV2Dto> mapToDtos(List<TableauBordV2> entities, RoleAccesService roleAccesService) {
        return entities.stream().map(entity -> mapToDto(entity, roleAccesService)).toList();
    }

    public List<TableauBordV2CategorieDto> mapCategoriesToDtos(List<TableauBordV2Categorie> categories) {
        return categories.stream().map(this::mapCategorie).toList();
    }

    public TableauBordV2CategorieDto mapCategorieToDto(TableauBordV2Categorie categorie) {
        return mapCategorie(categorie);
    }

    private TableauBordV2CategorieDto mapCategorie(TableauBordV2Categorie categorie) {
        if (categorie == null) {
            return null;
        }
        TableauBordV2CategorieDto dto = new TableauBordV2CategorieDto();
        copyBase(categorie, dto);
        dto.setNom(categorie.getNom());
        dto.setLibelle(categorie.getLibelle());
        dto.setDescription(categorie.getDescription());
        dto.setOrdre(categorie.getOrdre());
        dto.setActif(categorie.getActif());
        dto.setTbDomaineId(categorie.getTbDomaine() != null ? categorie.getTbDomaine().getId() : null);
        dto.setTbDomaine(mapTBDomaine(categorie.getTbDomaine()));
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

    private TableauBordV2SourceRefDto mapIndicateur(Indicateur indicateur) {
        if (indicateur == null) {
            return null;
        }
        TableauBordV2SourceRefDto dto = new TableauBordV2SourceRefDto();
        dto.setId(indicateur.getId());
        dto.setNom(indicateur.getNom());
        dto.setTitre(indicateur.getTitre());
        dto.setUnite(indicateur.getUnite());
        return dto;
    }

    private TableauBordV2SourceRefDto mapGrapheConfiguration(GrapheConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        TableauBordV2SourceRefDto dto = new TableauBordV2SourceRefDto();
        dto.setId(configuration.getId());
        dto.setNom(configuration.getNom());
        return dto;
    }

    private TableauBordV2SourceRefDto mapChiffreCle(ChiffreCle chiffreCle) {
        if (chiffreCle == null) {
            return null;
        }
        TableauBordV2SourceRefDto dto = new TableauBordV2SourceRefDto();
        dto.setId(chiffreCle.getId());
        dto.setLibelle(chiffreCle.getLibelle());
        dto.setValeur(chiffreCle.getValeur());
        dto.setUnite(chiffreCle.getUnite());
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
