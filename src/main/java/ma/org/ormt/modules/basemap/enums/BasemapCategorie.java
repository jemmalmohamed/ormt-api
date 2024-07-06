package ma.org.ormt.modules.basemap.enums;

public enum BasemapCategorie {

    DRONE("drone"),
    AVION("avion"),
    SATELLITE("satellite");

    private final String description;

    BasemapCategorie(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
