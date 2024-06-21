package ma.org.ancfcc.pva.core.commun.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.org.ancfcc.pva.core.validators.groups.OnUpdate;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode
@MappedSuperclass
public class Dto<ID extends UUID> {

    @Schema(accessMode = AccessMode.READ_ONLY)
    @NotNull(groups = { OnUpdate.class })
    private ID id;

}