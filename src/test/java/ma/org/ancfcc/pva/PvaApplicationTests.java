package ma.org.ancfcc.pva;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(TestContainerLifecycleCallbacks.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PvaApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
