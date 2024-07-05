package ma.org.ormt;

import java.time.Duration;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
public class TestContainerLifecycleCallbacks implements BeforeEachCallback {

    private static MSSQLServerContainer<?> mssqlServerContainer;

    static {
        DockerImageName dockerImageName = DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest");
        mssqlServerContainer = new MSSQLServerContainer<>(dockerImageName)
                .withStartupTimeout(Duration.ofSeconds(20))
                .acceptLicense();
        mssqlServerContainer.start();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put("mssqlServerContainer", mssqlServerContainer);
    }
}