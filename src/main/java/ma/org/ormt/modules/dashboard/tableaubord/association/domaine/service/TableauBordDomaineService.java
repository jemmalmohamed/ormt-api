package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.service;

import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.ReorderTBDomaineItem;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.TableauBordDomaineRequestDto;

public interface TableauBordDomaineService extends BaseService<TableauBordDomaine> {

        List<TableauBordDomaine> attachDomainesToTableauBord(
                        List<TableauBordDomaineRequestDto> tableaubordDomaineRequestDto);

        void detachDomainesFromTableauBord(List<Long> tableaubordDomaineIds);

        void reorderDomaines(Long tableaubordId,
                        List<ReorderTBDomaineItem> items);

}