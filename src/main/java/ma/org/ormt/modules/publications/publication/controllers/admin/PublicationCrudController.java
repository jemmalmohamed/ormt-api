package ma.org.ormt.modules.publications.publication.controllers.admin;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDto;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDtoMapper;
import ma.org.ormt.modules.publications.publication.dtos.request.PublicationRequestDto;
import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.modules.publications.publication.services.PublicationService;

@Log4j2
@RestController
@RequestMapping(value = "/api/v1/admin/publications")
@RequiredArgsConstructor
@Tag(name = "Publication", description = "Publication API")
public class PublicationCrudController extends BaseController<Publication> {

    private static final String ENTITY_NAME = "publication";

    private final PublicationService publicationService;
    private final PublicationDtoMapper publicationDtoMapper;

    @Operation(summary = "create " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationDto.class))),
            @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('publication:create')")
    public ResponseEntity<RestResponse<PublicationDto>> createPublication(
            @Validated(OnCreate.class) @ModelAttribute PublicationRequestDto requestDto) {
        try {

            // Create the publication with the photo URL
            Publication publication = publicationService.create(requestDto);
            return buildResponseEntity(publication, PublicationDto.class, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating publication: ", e);
            throw new RuntimeException("Failed to create publication", e);
        }
    }

    // *********** UPDATE OPERATIONS ***********

    @Operation(summary = "Update " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationDto.class))),
            @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('publication:edit')")
    public ResponseEntity<RestResponse<PublicationDto>> updatePublication(@PathVariable Long id,
            @Validated(OnUpdate.class) @ModelAttribute PublicationRequestDto publicationRequestDto)
            throws Exception {
        Publication publication = publicationService.update(id, publicationRequestDto);
        return buildResponseEntity(publication, PublicationDto.class, HttpStatus.OK);
    }

    // *********** DELETE OPERATIONS ***********

    @Operation(summary = "Delete " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return handleDelete(() -> publicationService.delete(id));
    }

    @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/bulk")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
        try {
            publicationService.deleteAllById(ids);
            return buildResponseEntity(ids, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            RestResponse<List<Long>> errorResponse = RestResponse.<List<Long>>builder()
                    .success(false)
                    .message("Suppression impossible, les données sont utilisées ailleurs")
                    .data(ids)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/all")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<Void> deleteAll() {
        return handleDelete(publicationService::deleteAll);
    }

    @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/exclude")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
        return handleDelete(() -> publicationService.deleteAllExceptIds(ids));
    }

    @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/query")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
            @RequestParam(value = "filters", defaultValue = "") List<String> filters,
            @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

        List<Long> deletedIds = publicationService.deleteBySpecification(filters, globalFilter,
                Publication.class);
        return buildResponseEntity(deletedIds, HttpStatus.OK);

    }

    @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/query-exclude")
    @PreAuthorize("hasAuthority('publication:delete')")
    public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
            @RequestBody List<Long> ids,
            @RequestParam(value = "filters", defaultValue = "") List<String> filters,
            @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

        List<Long> deletedIds = publicationService.deleteBySpecificationExceptIds(filters, globalFilter,
                Publication.class, ids);

        return buildResponseEntity(deletedIds, HttpStatus.OK);

    }

    @Override
    protected <DTO> DTO mapToDto(Publication entity, Class<DTO> dtoClass) {
        return dtoClass.cast(publicationDtoMapper.mapToDto(entity));
    }

}