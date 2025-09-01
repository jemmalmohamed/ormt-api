package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service;

import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.ReorderIndicateurItem;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.TBDomaineIndicateurRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

public interface TBDomaineIndicateurService extends BaseService<TBDomaine> {

        List<TBDomaineIndicateur> attachIndicateursToTBDomaine(
                        List<TBDomaineIndicateurRequestDto> tbDomaineIndicateurRequestDto);

        void detachIndicateursFromTBDomaine(List<Long> tbDomaineIndicateurIds);

        void reorderIndicateurs(Long tbDomaineId,
                        List<ReorderIndicateurItem> items);

}