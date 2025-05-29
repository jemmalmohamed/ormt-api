package ma.org.ormt.core.commun.base.controller;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.RestResponseUtil;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.security.authentication.services.AuthService;

@RequiredArgsConstructor
public abstract class BaseController<T> {

    @Autowired
    protected RoleAccesService roleAccesService;

    @Autowired
    protected AuthService authService;

    /**
     * Generic method to get entities with role-based access control
     */
    protected <S> Page<T> getEntitiesWithAccessControl(
            String resourceType,
            String permission,
            QueryParams requestParams,
            Function<QueryParams, Page<T>> getAllEntities,
            BiFunction<List<Long>, QueryParams, Page<T>> getEntitiesByIds) {

        List<Long> accessibleIds = roleAccesService.getAccessibleResourceIdsForCurrentUser(resourceType, permission);

        if (accessibleIds == null) {
            // Admin/Master - access to all
            return getAllEntities.apply(requestParams);
        } else if (accessibleIds.isEmpty()) {
            // No access - return empty page
            return Page.empty();
        } else {
            // Limited access - filter by accessible IDs
            return getEntitiesByIds.apply(accessibleIds, requestParams);
        }
    }

    /**
     * Check access for single resource
     */
    protected boolean hasResourceAccess(Long resourceId, String resourceType, String permission) {
        return roleAccesService.hasAccessToResource(resourceId, resourceType, permission);
    }

    protected abstract <DTO> DTO mapToDto(T entity, Class<DTO> dtoClass);

    public <DTO> ResponseEntity<RestResponse<DTO>> buildResponseEntity(T entity, Class<DTO> dtoClass,
            HttpStatus status) {
        DTO dto = mapToDto(entity, dtoClass);
        RestResponse<DTO> restResponse = RestResponse.<DTO>builder()
                .status(status)
                .data(dto)
                .build();
        return ResponseEntity.status(status).body(restResponse);
    }

    public <DTO> ResponseEntity<RestResponse<List<DTO>>> buildResponseEntity(List<T> entities, Class<DTO> dtoClass,
            HttpStatus status) {
        List<DTO> dtoList = entities.stream()
                .map(entity -> mapToDto(entity, dtoClass))
                .collect(Collectors.toList());

        RestResponse<List<DTO>> restResponse = RestResponse.<List<DTO>>builder()
                .status(status)
                .data(dtoList)
                .build();
        return ResponseEntity.status(status).body(restResponse);
    }

    public ResponseEntity<RestResponse<List<Long>>> buildResponseEntity(List<Long> ids, HttpStatus status) {
        return ResponseEntity.ok(RestResponse.<List<Long>>builder()
                .status(status)
                .data(ids)
                .message(!ids.isEmpty() ? null : "No data found")
                .build());
    }

    public <DTO> ResponseEntity<RestResponse<List<DTO>>> buildResponseEntity(List<T> entities, Class<DTO> dtoClass,
            QueryParams queryParams, HttpStatus status) {
        List<DTO> dtoList = entities.stream()
                .map(entity -> mapToDto(entity, dtoClass))
                .collect(Collectors.toList());
        RestResponse<List<DTO>> restResponse = RestResponseUtil.buildRestResponse(dtoList, queryParams);
        return ResponseEntity.status(status).body(restResponse);
    }

    public ResponseEntity<Void> handleDelete(Runnable deleteAction) {
        deleteAction.run();
        return ResponseEntity.noContent().build();
    }

    /**
     * Builds QueryParams object with safe handling of null filters.
     */
    public QueryParams buildQueryParams(int pageIndex, int pageSize, String sortField,
            Direction direction, List<String> filters, String globalFilter) {
        final List<String> safeFilters = filters != null ? filters : Collections.emptyList();

        return createQueryParams(pageIndex, pageSize, sortField, direction, safeFilters, globalFilter);
    }

    public QueryParams createQueryParams(int pageIndex, int pageSize, String sortField, Direction direction,
            List<String> filters, String globalFilter) {
        if (pageSize == -1) {
            pageSize = Integer.MAX_VALUE; // Set pageSize to a large number to fetch all records
        }
        return new QueryParams(pageIndex, pageSize, sortField, direction, filters, globalFilter);
    }

    public QueryParams adjustQueryParamsToGetAllRecords(QueryParams requestParams, Page<T> entitiesPage) {
        QueryParams queryParams = QueryParams.buildQueryParams(requestParams, entitiesPage);
        if (requestParams.getPageSize() == Integer.MAX_VALUE) {
            queryParams.setPageSize((int) queryParams.getTotalElements());
        }
        return queryParams;
    }

    /**
     * Create a forbidden response with appropriate message
     */
    public <U> ResponseEntity<RestResponse<U>> createForbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new RestResponse<>(HttpStatus.FORBIDDEN, "Permission denied", false, null, null));
    }

    /**
     * Create a not found response with error message
     */
    public <U> ResponseEntity<RestResponse<U>> createNotFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RestResponse<>(HttpStatus.NOT_FOUND, message, false, null, null));
    }

}
