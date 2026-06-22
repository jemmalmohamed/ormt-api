package ma.org.ormt.modules.analytics.domain.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DomaineAnalytiqueLinkRequestDto {

    @NotNull(message = "Ce champ est requis.")
    private Long domaineAnalytiqueId;

    private Integer ordre;
}
