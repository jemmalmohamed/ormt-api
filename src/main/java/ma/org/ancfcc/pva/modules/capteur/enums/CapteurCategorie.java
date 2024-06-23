package ma.org.ancfcc.pva.modules.capteur.enums;

public enum CapteurCategorie {

    DRONE("drone"),
    AVION("avion"),
    SATELLITE("satellite");

    private final String description;

    CapteurCategorie(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
