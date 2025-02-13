package ma.org.ormt.modules.gis.basemap.enums;

public enum BasemapMode {

    NUMERIQUE("numérique"),
    LIDAR("lidar"),
    ANALOGIQUE("analogique");

    private final String description;

    BasemapMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
