package ma.org.ormt.modules.dashboard.tbd.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardSummaryDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdAssignationDto;
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
import ma.org.ormt.modules.dashboard.tbd.dtos.request.TbdWidgetUpdateIndicatorRequest;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;

@RestController
@RequestMapping("/api/v1/tbd/dashboard")
@RequiredArgsConstructor
@Tag(name = "TBD Dashboard", description = "Dynamic Dashboard API")
public class TbdDashboardController {

    private final TbdDashboardService service;

    @Operation(summary = "List all dashboards (paginated)")
    @GetMapping("")
    public ResponseEntity<RestResponse<Page<TbdDashboardSummaryDto>>> findAll(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("lastModifiedDate").descending());
        Page<TbdDashboardSummaryDto> page = service.findAll(pageable);
        return ResponseEntity.ok(RestResponse.<Page<TbdDashboardSummaryDto>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(page)
                .build());
    }

    @Operation(summary = "Get dashboard assigned to a categorie (admin, any status)")
    @GetMapping("/admin/by-categorie/{categorieId}")
    public ResponseEntity<RestResponse<TbdDashboardSummaryDto>> findByCategorieAdmin(@PathVariable Long categorieId) {
        return service.findAssignedByCategorieAdmin(categorieId)
                .map(dto -> ResponseEntity.ok(RestResponse.<TbdDashboardSummaryDto>builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .data(dto)
                        .build()))
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get full dashboard by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<TbdDashboardFullDto>> findById(@PathVariable Long id) {
        TbdDashboardFullDto dto = service.findById(id);
        return ResponseEntity.ok(RestResponse.<TbdDashboardFullDto>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(dto)
                .build());
    }

    @Operation(summary = "List assigned category ids")
    @GetMapping("/assigned-categories")
    public ResponseEntity<RestResponse<List<Long>>> findAssignedCategoryIds(
            @RequestParam(required = false) Long excludeDashboardId) {
        List<Long> ids = service.findAssignedCategoryIds(excludeDashboardId);
        return ResponseEntity.ok(RestResponse.<List<Long>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(ids)
                .build());
    }

    @Operation(summary = "Create a new dashboard")
    @PostMapping("")
    public ResponseEntity<RestResponse<TbdDashboard>> create(
            @Validated @RequestBody TbdDashboardCreateRequest request) {
        TbdDashboard dashboard = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.<TbdDashboard>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .data(dashboard)
                .build());
    }

    @Operation(summary = "Update dashboard metadata")
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<TbdDashboard>> update(
            @PathVariable Long id,
            @Validated @RequestBody TbdDashboardUpdateRequest request) {
        TbdDashboard dashboard = service.update(id, request);
        return ResponseEntity.ok(RestResponse.<TbdDashboard>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(dashboard)
                .build());
    }

    @Operation(summary = "Duplicate a dashboard")
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<RestResponse<TbdDashboard>> duplicate(@PathVariable Long id) {
        TbdDashboard dashboard = service.duplicate(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.<TbdDashboard>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .data(dashboard)
                .build());
    }

    @Operation(summary = "Delete a dashboard")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Publish a dashboard")
    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable Long id) {
        service.publish(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Set dashboard as draft")
    @PutMapping("/{id}/draft")
    public ResponseEntity<Void> setDraft(@PathVariable Long id) {
        service.setDraft(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive a dashboard")
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        service.archive(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign dashboard to an analytic category")
    @PutMapping("/{id}/assign")
    public ResponseEntity<RestResponse<TbdAssignationDto>> assign(
            @PathVariable Long id,
            @Validated @RequestBody TbdDashboardAssignRequest request) {
        TbdAssignationDto assignation = service.assign(id, request);
        return ResponseEntity.ok(RestResponse.<TbdAssignationDto>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(assignation)
                .build());
    }

    @Operation(summary = "Remove dashboard assignation")
    @DeleteMapping("/{id}/assign")
    public ResponseEntity<Void> removeAssignation(@PathVariable Long id) {
        service.removeAssignation(id);
        return ResponseEntity.noContent().build();
    }

    // --- Sections ---

    @Operation(summary = "Add a section to a dashboard")
    @PostMapping("/{id}/sections")
    public ResponseEntity<RestResponse<TbdSection>> addSection(
            @PathVariable Long id,
            @RequestBody TbdSectionCreateRequest request) {
        TbdSection section = service.addSection(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.<TbdSection>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .data(section)
                .build());
    }

    @Operation(summary = "Resize sections (splitter save)")
    @PutMapping("/{id}/sections/resize")
    public ResponseEntity<Void> resizeSections(
            @PathVariable Long id,
            @Validated @RequestBody TbdSectionResizeRequest request) {
        service.resizeSections(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reorder sections")
    @PutMapping("/{id}/sections/reorder")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long id,
            @RequestBody List<Long> orderedSectionIds) {
        service.reorderSections(id, orderedSectionIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a section")
    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        service.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    // --- Rows ---

    @Operation(summary = "Add a row to a section")
    @PostMapping("/sections/{sectionId}/rows")
    public ResponseEntity<RestResponse<TbdWidgetRow>> addRow(
            @PathVariable Long sectionId,
            @RequestBody TbdWidgetRowCreateRequest request) {
        TbdWidgetRow row = service.addRow(sectionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.<TbdWidgetRow>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .data(row)
                .build());
    }

    @Operation(summary = "Resize rows (splitter save)")
    @PutMapping("/sections/{sectionId}/rows/resize")
    public ResponseEntity<Void> resizeRows(
            @PathVariable Long sectionId,
            @Validated @RequestBody TbdRowResizeRequest request) {
        service.resizeRows(sectionId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reorder rows")
    @PutMapping("/sections/{sectionId}/rows/reorder")
    public ResponseEntity<Void> reorderRows(
            @PathVariable Long sectionId,
            @RequestBody List<Long> orderedRowIds) {
        service.reorderRows(sectionId, orderedRowIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update row height")
    @PutMapping("/rows/{rowId}/height")
    public ResponseEntity<Void> updateRowHeight(
            @PathVariable Long rowId,
            @Validated @RequestBody TbdWidgetRowHeightUpdateRequest request) {
        service.updateRowHeight(rowId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a row")
    @DeleteMapping("/rows/{rowId}")
    public ResponseEntity<Void> deleteRow(@PathVariable Long rowId) {
        service.deleteRow(rowId);
        return ResponseEntity.noContent().build();
    }

    // --- Widgets ---

    @Operation(summary = "Add a widget to a row")
    @PostMapping("/rows/{rowId}/widgets")
    public ResponseEntity<RestResponse<TbdWidget>> addWidget(
            @PathVariable Long rowId,
            @Validated @RequestBody TbdWidgetCreateRequest request) {
        request.setRowId(rowId);
        TbdWidget widget = service.addWidget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.<TbdWidget>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .data(widget)
                .build());
    }

    @Operation(summary = "Resize widgets (splitter save)")
    @PutMapping("/rows/{rowId}/widgets/resize")
    public ResponseEntity<Void> resizeWidgets(
            @PathVariable Long rowId,
            @Validated @RequestBody TbdWidgetResizeRequest request) {
        service.resizeWidgets(rowId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reorder widgets")
    @PutMapping("/rows/{rowId}/widgets/reorder")
    public ResponseEntity<Void> reorderWidgets(
            @PathVariable Long rowId,
            @RequestBody List<Long> orderedWidgetIds) {
        service.reorderWidgets(rowId, orderedWidgetIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a widget")
    @DeleteMapping("/widgets/{widgetId}")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long widgetId) {
        service.deleteWidget(widgetId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update widget content (EDITOR / TEXT)")
    @PutMapping("/widgets/{widgetId}/content")
    public ResponseEntity<Void> updateWidgetContent(
            @PathVariable Long widgetId,
            @RequestBody TbdWidgetUpdateContentRequest request) {
        service.updateWidgetContent(widgetId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update widget indicator source (CHART)")
    @PutMapping("/widgets/{widgetId}/source")
    public ResponseEntity<Void> updateWidgetIndicator(
            @PathVariable Long widgetId,
            @RequestBody TbdWidgetUpdateIndicatorRequest request) {
        service.updateWidgetIndicator(widgetId, request);
        return ResponseEntity.noContent().build();
    }
}
