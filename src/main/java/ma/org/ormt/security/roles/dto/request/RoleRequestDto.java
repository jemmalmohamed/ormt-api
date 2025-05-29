package ma.org.ormt.security.roles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.core.validators.groups.OnUpdate;

@Setter
@Getter
@AllArgsConstructor
public class RoleRequestDto {

    @Schema(accessMode = AccessMode.READ_ONLY)
    @NotNull(groups = { OnUpdate.class })
    private String id;

    @NotNull(message = "Ce champ est requis.")
    private String name;

    @NotNull(message = "Ce champ est requis.")
    private String description;

}
