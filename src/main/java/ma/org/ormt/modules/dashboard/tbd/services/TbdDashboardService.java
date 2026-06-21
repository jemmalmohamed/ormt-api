package ma.org.ormt.modules.dashboard.tbd.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardSummaryDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardAssignRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdDashboardUpdateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdRowResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdSectionCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdSectionResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetResizeRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetRowCreateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetRowHeightUpdateRequest;
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetUpdateContentRequest;
import ma.org.ormt.modules.dashboard.tbd.models.TbdAssignation;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;

public interface TbdDashboardService {

    TbdDashboardFullDto findById(Long id);

    Page<TbdDashboardSummaryDto> findAll(Pageable pageable);

    TbdDashboard create(TbdDashboardCreateRequest request);

    TbdDashboard update(Long id, TbdDashboardUpdateRequest request);

    TbdDashboard duplicate(Long id);

    List<Long> findAssignedCategoryIds(Long excludeDashboardId);

    void delete(Long id);

    void publish(Long id);

    void setDraft(Long id);

    void archive(Long id);

    TbdAssignation assign(Long dashboardId, TbdDashboardAssignRequest request);

    void removeAssignation(Long dashboardId);

    TbdSection addSection(Long dashboardId, TbdSectionCreateRequest request);

    void resizeSections(Long dashboardId, TbdSectionResizeRequest request);

    void reorderSections(Long dashboardId, List<Long> orderedSectionIds);

    void deleteSection(Long sectionId);

    TbdWidgetRow addRow(Long sectionId, TbdWidgetRowCreateRequest request);

    void resizeRows(Long sectionId, TbdRowResizeRequest request);

    void reorderRows(Long sectionId, List<Long> orderedRowIds);

    void updateRowHeight(Long rowId, TbdWidgetRowHeightUpdateRequest request);

    void deleteRow(Long rowId);

    TbdWidget addWidget(TbdWidgetCreateRequest request);

    void resizeWidgets(Long rowId, TbdWidgetResizeRequest request);

    void reorderWidgets(Long rowId, List<Long> orderedWidgetIds);

    void deleteWidget(Long widgetId);

    void updateWidgetContent(Long widgetId, TbdWidgetUpdateContentRequest request);
}
