package ma.org.ormt.core.commun.rest;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestResponseUtil {

        public static <T> RestResponse<T> buildRestResponse(T data) {
                return RestResponse.<T>builder()
                                .status(HttpStatus.OK)
                                .data(data)
                                .message(data != null
                                                ? null
                                                : "no data found")
                                .build();
        }

        public static <T> RestResponse<T> buildRestResponse(T data, QueryParams queryParams) {
                return RestResponse.<T>builder()
                                .status(HttpStatus.OK)
                                .data(data)
                                .queryParams(queryParams)
                                .message(data != null
                                                ? null
                                                : "no data found")
                                .build();
        }
}
