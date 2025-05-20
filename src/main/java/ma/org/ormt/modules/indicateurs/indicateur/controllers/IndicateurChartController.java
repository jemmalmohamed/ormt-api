package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.series.IndicateurChartDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.charts.IndicateurChartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/indicateurs")
@Tag(name = "Indicateur Chart API", description = "API for managing indicateur chart data")
public class IndicateurChartController {

    private final IndicateurChartService indicateurChartService;

    @GetMapping("/{id}/chart")
    @Operation(summary = "Get chart data for an indicator")
    public ResponseEntity<IndicateurChartDto> getChartData(@PathVariable Long id) {
        IndicateurChartDto chartDto = indicateurChartService.getChartData(id);
        return ResponseEntity.ok(chartDto);
    }

    @PostMapping("/{id}/chart/filter")
    @Operation(summary = "Get filtered chart data for an indicator")
    public ResponseEntity<IndicateurChartDto> getFilteredChartData(
            @PathVariable Long id,
            @RequestBody Map<String, List<String>> filters) {
        IndicateurChartDto chartDto = indicateurChartService.getFilteredChartData(id, filters);
        return ResponseEntity.ok(chartDto);
    }
}