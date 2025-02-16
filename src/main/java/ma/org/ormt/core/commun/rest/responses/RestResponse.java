package ma.org.ormt.core.commun.rest.responses;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ma.org.ormt.core.commun.rest.queries.QueryParams;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response object with status, success indicator, message, and data payload.")
public class RestResponse<T> {

    private HttpStatus status;

    private String message;

    private boolean success;

    private T data;

    private QueryParams queryParams;

}