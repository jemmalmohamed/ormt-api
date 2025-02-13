package ma.org.ormt.modules.gis.basemap.enums;

public enum BasemapName {

    ADS40_80("ADS40/80"),
    DMC_II_230("DMC_II_230"),
    ALS70("ALS70"),
    RC30("RC30"),
    RMK_TOP_15("RMK_TOP_15");

    private final String description;

    BasemapName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
