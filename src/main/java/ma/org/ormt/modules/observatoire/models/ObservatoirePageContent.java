package ma.org.ormt.modules.observatoire.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "observatoire_page_content")
public class ObservatoirePageContent extends BaseEntity {

    private String kicker;

    private String badgeTitle;

    private String badgeSubtitle;

    private String heroTagline;

    @Column(columnDefinition = "TEXT")
    private String introText;

    @Column(columnDefinition = "TEXT")
    private String visionText;

    @Column(columnDefinition = "TEXT")
    private String missionText;

    @Column(columnDefinition = "TEXT")
    private String partnershipText;

    @Column(columnDefinition = "TEXT")
    private String objectivesJson;

    @Column(columnDefinition = "TEXT")
    private String pillarsJson;

    @Column(columnDefinition = "TEXT")
    private String teamJson;

    @Column(columnDefinition = "TEXT")
    private String actionsJson;

    private boolean actif;

    private boolean published;
}