package com.connectflow.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "app.jwt.secret=VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JDb25uZWN0Rmxvdw==",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=never"
})
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void readsExistingAdministrativeRoleWithoutChangingDatabase() {
        assertTrue(roleRepository.findByRoleNameIgnoreCase("Administrative").isPresent());
    }
}
