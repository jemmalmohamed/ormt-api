package ma.org.ancfcc.pva.core.commun.rest.responses;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "Error Response", description = "Standard API error response.")
public class ErrorResponse {

    private String status;
    private LocalDateTime dateTime;
    private String message;
    private String details;
    private String errorType;

}