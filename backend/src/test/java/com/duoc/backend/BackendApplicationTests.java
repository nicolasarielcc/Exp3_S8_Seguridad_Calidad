package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		useMainMethod = SpringBootTest.UseMainMethod.WHEN_AVAILABLE,
		properties = {
				"spring.main.web-application-type=none",
				"spring.main.banner-mode=off",
				"logging.level.root=OFF",
				"spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
				"spring.datasource.driverClassName=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=create-drop",
				"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
		}
)
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
