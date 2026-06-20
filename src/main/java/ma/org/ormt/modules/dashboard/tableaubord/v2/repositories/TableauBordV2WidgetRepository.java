package ma.org.ormt.modules.dashboard.tableaubord.v2.repositories;

import java.util.List;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Widget;

public interface TableauBordV2WidgetRepository extends BaseRepository<TableauBordV2Widget> {

    List<TableauBordV2Widget> findByDashboardIdOrderByOrdreAscIdAsc(Long dashboardId);
}
