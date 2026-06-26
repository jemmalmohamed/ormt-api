package ma.org.ormt.modules.observatoire.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObservatoireActionCardDto {

    private String badge;

    private String title;

    private String description;

    private String link;

    private Boolean external;

    private String cta;
}