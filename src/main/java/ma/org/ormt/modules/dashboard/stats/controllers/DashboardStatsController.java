package ma.org.ormt.modules.dashboard.stats.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.RestResponseUtil;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.stats.dtos.DashboardStatsDto;
import ma.org.ormt.modules.dashboard.stats.services.DashboardStatsService;

@RestController
@RequestMapping("/api/v1/public/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard Statistics API")
public class DashboardStatsController {

    private final DashboardStatsService statsService;

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated dashboard statistics")
    public ResponseEntity<RestResponse<DashboardStatsDto>> getStats() {
        DashboardStatsDto stats = statsService.getStats();
        RestResponse<DashboardStatsDto> response = RestResponseUtil.buildRestResponse(stats);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
