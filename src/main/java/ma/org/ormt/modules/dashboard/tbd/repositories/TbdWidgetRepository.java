package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;

@Repository
public interface TbdWidgetRepository extends BaseRepository<TbdWidget> {

    List<TbdWidget> findByRowIdOrderByOrdreAsc(Long rowId);

    List<TbdWidget> findByRowIdInOrderByRowIdAscOrdreAsc(List<Long> rowIds);
}
