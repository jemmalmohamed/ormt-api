package ma.org.ormt.core.commun.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.core.validators.groups.OnUpdate;

@Setter
@Getter
@EqualsAndHashCode
@MappedSuperclass
public class Dto {

    @Schema(accessMode = AccessMode.READ_ONLY)
    @NotNull(groups = { OnUpdate.class })
    private Long id;

}