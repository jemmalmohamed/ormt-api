package ma.org.ormt.modules.chiffres.association.domaine.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.association.domaine.dtos.ChiffreCleDomaineDto;
import ma.org.ormt.modules.chiffres.association.domaine.dtos.ChiffreCleDomaineDtoMapper;
import ma.org.ormt.modules.chiffres.association.domaine.dtos.request.ChiffreCleDomaineRequestDto;
import ma.org.ormt.modules.chiffres.association.domaine.service.ChiffreCleDomaineService;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

@RestController
@RequestMapping(value = "/api/v1/chiffrecle-domaine")
@RequiredArgsConstructor
@Tag(name = "ChiffreCle", description = "ChiffreCle API")
public class ChiffreCleDomaineCrudController extends BaseController<ChiffreCleDomaine> {

        private static final String ENTITY_NAME = "chiffrecle";

        private final ChiffreCleDomaineService chiffrecleDomaineService;
        private final ChiffreCleDomaineDtoMapper chiffrecleDomainesDtoMapper;

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "attach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChiffreCle.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-domaines")
        @PreAuthorize("hasAuthority('chiffrecle:edit')")
        public ResponseEntity<RestResponse<List<Long>>> attachDomainessToChiffreCle(
                        @Validated(OnCreate.class) @RequestBody List<ChiffreCleDomaineRequestDto> request) {

                List<ChiffreCleDomaine> chiffrecleDomaines = chiffrecleDomaineService
                                .attachDomainesToChiffreCle(request);

                List<Long> ids = chiffrecleDomaines.stream().map(ChiffreCleDomaine::getId).toList();

                return buildResponseEntity(ids, HttpStatus.OK, true);
        }

        @Operation(summary = "detach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChiffreCle.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-domaines")
        @PreAuthorize("hasAuthority('chiffrecle:edit')")
        public ResponseEntity<RestResponse<List<Long>>> detachDomainesFromChiffreCle(
                        @Validated(OnCreate.class) @RequestBody List<Long> deletedIds) {
                chiffrecleDomaineService.detachDomainesFromChiffreCle(deletedIds);
                return buildResponseEntity(deletedIds, HttpStatus.OK, true);
        }

        @Override
        protected <DTO> DTO mapToDto(ChiffreCleDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == ChiffreCleDomaineDto.class) {
                        return dtoClass.cast(chiffrecleDomainesDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}