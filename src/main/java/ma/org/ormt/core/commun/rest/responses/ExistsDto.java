package ma.org.ormt.core.commun.rest.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Existence check payload with the existing entity id and optional excluded id.")
public class ExistsDto {

    private boolean exists;

    private Long existingId;

    private Long excludeId;

    public ExistsDto(boolean exists, Long existingId) {
        this.exists = exists;
        this.existingId = existingId;
    }
}
