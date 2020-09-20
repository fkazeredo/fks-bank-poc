package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.integration.UserRequestFactory
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PermissionResourceIT {

    private static final String BASE_URL = "/permissions"

    @LocalServerPort
    Integer port

    @BeforeEach
    void setUp() {
        RestAssured.port = port
    }

    @Test
    void "GIVEN a manager user profile, WHEN find all permissions, THEN should return '200' success status code with the permissions list"() {

        def permissions = UserRequestFactory.withManager()
                .get(BASE_URL)
                .then()
                .statusCode(200).extract().as(List)

        assert 2 == permissions.size()
        assert permissions.contains([name: "MANAGER", description: "Gerente"])
        assert permissions.contains([name: "CUSTOMER", description: "Cliente"])

    }

    @Test
    void "GIVEN a customer user profile, WHEN find all permissions, THEN should return '403' error status"() {

        UserRequestFactory.withCustomer()
                .get(BASE_URL)
                .then()
                .statusCode(403)

    }

}
