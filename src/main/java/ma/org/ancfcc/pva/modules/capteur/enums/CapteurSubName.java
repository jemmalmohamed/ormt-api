package ma.org.ancfcc.pva.modules.capteur.enums;

public enum CapteurSubName {

    ADS("ads"),
    DMC("dmc"),
    ALS("als"),
    RC30("rc"),
    RMK("rmk");

    private final String description;

    CapteurSubName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
