package ma.org.ancfcc.pva.core.commun.base.dto;

import java.time.LocalDate;

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
    private LocalDate createdDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private LocalDate lastModifiedDate;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private String lastModifiedBy;

}