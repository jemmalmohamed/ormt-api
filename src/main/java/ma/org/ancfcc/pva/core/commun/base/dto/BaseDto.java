package ma.org.ancfcc.pva.core.commun.base.dto;

import java.time.LocalDateTime;

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
    private LocalDateTime createdDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private LocalDateTime lastModifiedDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private String lastModifiedBy;

}