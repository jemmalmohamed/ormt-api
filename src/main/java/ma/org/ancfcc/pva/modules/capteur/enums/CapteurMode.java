package ma.org.ancfcc.pva.modules.capteur.enums;

public enum CapteurMode {

    NUMERIQUE("numérique"),
    LIDAR("lidar"),
    ANALOGIQUE("analogique");

    private final String description;

    CapteurMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
