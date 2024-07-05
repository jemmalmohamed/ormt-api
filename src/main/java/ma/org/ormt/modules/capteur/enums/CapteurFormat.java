package ma.org.ormt.modules.capteur.enums;

public enum CapteurFormat {

    LINEAIRE("linéaire"),
    MATRICIELLE("matricielle");

    private final String description;

    CapteurFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
