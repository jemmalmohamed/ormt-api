package ma.org.ormt.core.commun.base.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public class BaseDto extends Dto {

    @Schema(accessMode = AccessMode.READ_ONLY)
    private Integer statusCode;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty("dateCreation")
    private LocalDateTime createdDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty("dateDerniereModification")
    private LocalDateTime lastModifiedDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty("creePar")
    private String createdBy;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty("modifiePar")
    private String lastModifiedBy;

}