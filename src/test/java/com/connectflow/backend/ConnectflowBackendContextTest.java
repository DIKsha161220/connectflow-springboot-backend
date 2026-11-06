package com.connectflow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "app.jwt.secret=VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JDb25uZWN0Rmxvdw==",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=never"
})
class ConnectflowBackendContextTest {

    @Test
    void contextStartsAgainstTheExistingSchema() {
    }
}
