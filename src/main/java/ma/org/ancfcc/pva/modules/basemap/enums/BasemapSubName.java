package ma.org.ancfcc.pva.modules.basemap.enums;

public enum BasemapSubName {

    ADS("ads"),
    DMC("dmc"),
    ALS("als"),
    RC30("rc"),
    RMK("rmk");

    private final String description;

    BasemapSubName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
