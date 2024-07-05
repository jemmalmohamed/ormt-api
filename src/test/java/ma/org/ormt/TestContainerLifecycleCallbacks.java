package ma.org.ormt;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerLifecycleCallbacks implements BeforeEachCallback {

    private static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgis/postgis:15-3.3-alpine").asCompatibleSubstituteFor("postgres"));
        postgreSQLContainer.start();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put("postgreSQLContainer", postgreSQLContainer);
    }
}