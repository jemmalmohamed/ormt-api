package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;

@Repository
public interface TbdWidgetRowRepository extends BaseRepository<TbdWidgetRow> {

    List<TbdWidgetRow> findBySectionIdOrderByOrdreAsc(Long sectionId);

    List<TbdWidgetRow> findBySectionIdInOrderBySectionIdAscOrdreAsc(List<Long> sectionIds);

    List<TbdWidgetRow> findByIdIn(List<Long> ids);
}
