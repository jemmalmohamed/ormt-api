package ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml;

public enum EventType {

    START("Start"),
    END("End"),
    RELEASE("Release");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
