package ma.org.ancfcc.pva.modules.basemap.enums;

public enum BasemapFormat {

    LINEAIRE("linéaire"),
    MATRICIELLE("matricielle");

    private final String description;

    BasemapFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
