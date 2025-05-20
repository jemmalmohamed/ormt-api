package ma.org.ormt.modules.audit.indicateur.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.audit.indicateur.services.IndicateurExportService;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("api/v1/indicateurs/audit")
@RequiredArgsConstructor
public class IndicateurAuditController extends BaseController<Indicateur> {

        private static final String ENTITY_NAME = "indicateur";

        private final IndicateurService indicateurService;
        private final IndicateurDtoMapper indicateurDtoMapper;
        private final IndicateurDetailDtoMapper indicateurDetailMapper;
        private final IndicateurExportService indicateurExportService;

        @Operation(summary = "Exporter les indicateurs (nom et description) en Excel")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export OK", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/export")
        @PreAuthorize("hasAuthority('indicateur:list')")
        @ResponseBody
        public ResponseEntity<byte[]> exportIndicateurs() throws Exception {
                List<Indicateur> indicateurs = indicateurService.findAll();
                return indicateurExportService.exportIndicateursAudit(indicateurs);
        }

        @Override
        protected <DTO> DTO mapToDto(Indicateur entity, Class<DTO> dtoClass) {
                if (dtoClass == IndicateurDetailDto.class) {
                        return dtoClass.cast(indicateurDetailMapper.mapToDto(entity));
                } else if (dtoClass == IndicateurDto.class) {
                        return dtoClass.cast(indicateurDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
