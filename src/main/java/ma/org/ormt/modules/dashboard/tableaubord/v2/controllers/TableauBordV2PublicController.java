package ma.org.ormt.modules.dashboard.tableaubord.v2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2Dto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2Mapper;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.services.TableauBordV2Service;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

@RestController
@RequestMapping("/api/v1/public/dashboards-v2")
@RequiredArgsConstructor
@Tag(name = "TableauBordV2Public", description = "Tableau de bord dynamique V2 public API")
public class TableauBordV2PublicController {

    private static final String RESOURCE_TYPE = "tableauBordV2";
    private static final String PERMISSION_READ = "lecture";

    private final TableauBordV2Service service;
    private final TableauBordV2Mapper mapper;
    private final RoleAccesService roleAccesService;

    @GetMapping
    public ResponseEntity<RestResponse<java.util.List<TableauBordV2Dto>>> getPublishedDashboards() {
        java.util.List<Long> accessibleIds = roleAccesService
                .getAccessibleResourceIdsForCurrentUser(RESOURCE_TYPE, PERMISSION_READ);
        java.util.List<TableauBordV2> dashboards = accessibleIds == null
                ? service.findPublished()
                : service.findPublishedByIds(accessibleIds);
        return ResponseEntity.ok(RestResponse.<java.util.List<TableauBordV2Dto>>builder()
                .status(HttpStatus.OK)
                .success(true)
                .data(mapper.mapToDtos(dashboards, roleAccesService))
                .build());
    }

    @GetMapping("/{nom}")
    public ResponseEntity<RestResponse<TableauBordV2Dto>> getPublishedDashboard(@PathVariable String nom) {
        TableauBordV2 dashboard = service.findPublishedByNom(nom).orElseThrow();
        if (!roleAccesService.hasAccessToResource(dashboard.getId(), RESOURCE_TYPE, PERMISSION_READ)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(RestResponse.<TableauBordV2Dto>builder()
                .status(HttpStatus.OK)
                .success(true)
                .data(mapper.mapToDto(dashboard, roleAccesService))
                .build());
    }
}
