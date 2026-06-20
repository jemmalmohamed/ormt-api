package ma.org.ormt.modules.dashboard.tableaubord.v2.services;

import java.util.List;
import java.util.Optional;

import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2CategorieRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2RequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2WidgetRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Categorie;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Widget;

public interface TableauBordV2Service {

    List<TableauBordV2> findAll();

    Optional<TableauBordV2> findById(Long id);

    Optional<TableauBordV2> findPublishedByNom(String nom);

    List<TableauBordV2> findPublished();

    List<TableauBordV2> findPublishedByIds(List<Long> ids);

    List<TableauBordV2Categorie> findActiveCategories();

    void syncCategoriesFromLegacyDomaines();

    TableauBordV2Categorie createCategorie(TableauBordV2CategorieRequestDto requestDto);

    TableauBordV2Categorie updateCategorie(Long id, TableauBordV2CategorieRequestDto requestDto);

    void deleteCategorie(Long id);

    TableauBordV2 create(TableauBordV2RequestDto requestDto);

    TableauBordV2 update(Long id, TableauBordV2RequestDto requestDto);

    void delete(Long id);

    TableauBordV2Widget createWidget(Long dashboardId, TableauBordV2WidgetRequestDto requestDto);

    TableauBordV2Widget updateWidget(Long dashboardId, Long widgetId, TableauBordV2WidgetRequestDto requestDto);

    void deleteWidget(Long dashboardId, Long widgetId);

    List<TableauBordV2Widget> reorderWidgets(Long dashboardId, List<Long> widgetIds);
}
