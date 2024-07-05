package ma.org.ormt.core.commun.base.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.RestResponseUtil;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;

@RequiredArgsConstructor
public abstract class BaseController<T> {

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

    public ResponseEntity<RestResponse<List<Long>>> buildResponseEntity(List<Long> ids, HttpStatus status) {
        return ResponseEntity.ok(RestResponse.<List<Long>>builder()
                .status(status)
                .data(ids)
                .message(!ids.isEmpty() ? null : "No data found")
                .build());
    }

    public <DTO> ResponseEntity<RestResponse<List<DTO>>> buildResponseEntity(List<DTO> dtos,
            QueryParams queryParams, HttpStatus status) {
        RestResponse<List<DTO>> restResponse = RestResponseUtil.buildRestResponse(dtos, queryParams);
        return ResponseEntity.status(status).body(restResponse);
    }

    public ResponseEntity<Void> handleDelete(Runnable deleteAction) {
        deleteAction.run();
        return ResponseEntity.noContent().build();
    }

    public QueryParams createQueryParams(int pageIndex, int pageSize, String sortField, Direction direction,
            List<String> filters, String globalFilter) {
        if (pageSize == -1) {
            pageSize = Integer.MAX_VALUE; // Set pageSize to a large number to fetch all records
        }
        return new QueryParams(pageIndex, pageSize, sortField, direction, filters, globalFilter);
    }

    public QueryParams adjustQueryParamsForAllRecords(QueryParams requestParams, Page<T> entitiesPage) {
        QueryParams queryParams = QueryParams.buildQueryParams(requestParams, entitiesPage);
        if (requestParams.getPageSize() == Integer.MAX_VALUE) {
            queryParams.setPageSize((int) queryParams.getTotalElements());
        }
        return queryParams;
    }

}
