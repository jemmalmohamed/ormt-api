package ma.org.ormt.modules.dashboard.tableaubord.v2.controllers.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2Mapper;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2WidgetDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2WidgetRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.services.TableauBordV2Service;

@Validated
@RestController
@RequestMapping("/api/v1/admin/dashboards-v2/{dashboardId}/widgets")
@RequiredArgsConstructor
@Tag(name = "TableauBordV2Widget", description = "Widgets de tableau de bord dynamique V2 API")
public class TableauBordV2WidgetAdminController {

    private final TableauBordV2Service service;
    private final TableauBordV2Mapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TableauBordV2WidgetDto>> createWidget(
            @PathVariable Long dashboardId,
            @Validated @RequestBody TableauBordV2WidgetRequestDto requestDto) {
        return response(mapper.mapToDto(service.createWidget(dashboardId, requestDto)), HttpStatus.CREATED);
    }

    @PutMapping("/{widgetId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TableauBordV2WidgetDto>> updateWidget(
            @PathVariable Long dashboardId,
            @PathVariable Long widgetId,
            @Validated @RequestBody TableauBordV2WidgetRequestDto requestDto) {
        return response(mapper.mapToDto(service.updateWidget(dashboardId, widgetId, requestDto)), HttpStatus.OK);
    }

    @PutMapping("/order")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<List<TableauBordV2WidgetDto>>> reorderWidgets(
            @PathVariable Long dashboardId,
            @RequestBody List<Long> widgetIds) {
        List<TableauBordV2WidgetDto> widgets = service.reorderWidgets(dashboardId, widgetIds).stream()
                .map(mapper::mapToDto)
                .toList();
        return response(widgets, HttpStatus.OK);
    }

    @DeleteMapping("/{widgetId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> deleteWidget(
            @PathVariable Long dashboardId,
            @PathVariable Long widgetId) {
        service.deleteWidget(dashboardId, widgetId);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<RestResponse<T>> response(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(RestResponse.<T>builder()
                .status(status)
                .success(true)
                .data(data)
                .build());
    }
}
