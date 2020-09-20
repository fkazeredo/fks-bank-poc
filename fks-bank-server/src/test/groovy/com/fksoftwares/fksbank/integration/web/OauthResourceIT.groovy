package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.integration.UserRequestFactory
import com.fksoftwares.fksbank.userprofile.Permission
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OauthResourceIT {

    private static final String BASE_URL = "/auth/token"

    @LocalServerPort
    Integer port

    @Autowired
    JwtDecoder jwtDecoder

    @BeforeEach
    void setUp() {
        RestAssured.port = port
    }

    @Test
    void "GIVEN a manager user profile, WHEN request access token, THEN should return '200' success response and JWT Bearer access token"() {

        ExtractableResponse<Response> response = UserRequestFactory.loginManager()
                .then()
                .statusCode(200)
                .extract()

        assert response != null

        def accessToken = response.body().jsonPath().getString("access_token")
        def refreshToken = response.cookie("refreshToken")
        def tokenType = response.body().jsonPath().getString("token_type")
        def expiresIn = response.body().jsonPath().getInt("expires_in")

        assert accessToken != null
        assert refreshToken != null

        Jwt jwt = jwtDecoder.decode(accessToken)
        List<String> scopes = jwt.getClaim("scope") as List<String>
        List<String> authorities = jwt.getClaim("authorities") as List<String>

        assert 1L == jwt.getClaim("id")
        assert "Ricardo Admin" == jwt.getClaim("name")
        assert "admin@admin.com" == jwt.getClaim("user_name")

        assert scopes.contains("read")
        assert scopes.contains("write")

        assert authorities.contains(Permission.MANAGER.name())
        assert !authorities.contains(Permission.CUSTOMER.name())

        assert "bearer" == tokenType

        assert expiresIn <= 60

    }

    @Test
    void "GIVEN a customer user profile, WHEN request access token, THEN should return '200' success response and JWT Bearer access token"() {

        ExtractableResponse<Response> response = UserRequestFactory.loginCustomer()
                .then()
                .statusCode(200)
                .extract()

        assert response != null

        def accessToken = response.body().jsonPath().getString("access_token")
        def refreshToken = response.cookie("refreshToken")
        def tokenType = response.body().jsonPath().getString("token_type")
        def expiresIn = response.body().jsonPath().getInt("expires_in")

        assert accessToken != null
        assert refreshToken != null

        Jwt jwt = jwtDecoder.decode(accessToken)
        List<String> scopes = jwt.getClaim("scope") as List<String>
        List<String> authorities = jwt.getClaim("authorities") as List<String>

        assert 2L == jwt.getClaim("id")
        assert "João Customer" == jwt.getClaim("name")
        assert "customer@customer.com" == jwt.getClaim("user_name")

        assert scopes.contains("read")
        assert scopes.contains("write")

        assert authorities.contains(Permission.CUSTOMER.name())
        assert !authorities.contains(Permission.MANAGER.name())

        assert "bearer" == tokenType

        assert expiresIn <= 60

    }

    @Test
    void "GIVEN a disabled user profile, WHEN request access token, THEN should return '401' error response"() {

        RestAssured.given()
                .auth().basic("fkbank_ui", "Ab1234@")
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("application/x-www-form-urlencoded charset=UTF-8", ContentType.URLENC)))
                .formParam("client", "fkbank_ui")
                .formParam("username", "disabled@admin.com")
                .formParam("password", "Ab1234@")
                .formParam("grant_type", "password")
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(401)

    }

    @Test
    void "GIVEN an user profile that does not exist, WHEN request access token, THEN should return '401' error response"() {

        RestAssured.given()
                .auth().basic("fkbank_ui", "Ab1234@")
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("application/x-www-form-urlencoded charset=UTF-8", ContentType.URLENC)))
                .formParam("client", "fkbank_ui")
                .formParam("username", "invalid@admin.com")
                .formParam("password", "Ab1234@")
                .formParam("grant_type", "password")
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(401)

    }

    @Test
    void "GIVEN an authenticated user profile, WHEN revoke token, THEN should return '401' error response and erase refresh token from Cookie"() {

        ExtractableResponse<Response> loginResponse = UserRequestFactory.loginManager()
                .then()
                .extract()

        assert loginResponse != null

        def accessToken = loginResponse.body().jsonPath().getString("access_token")

        ExtractableResponse<Response> response = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .when()
                .delete(BASE_URL + "/revoke")
                .then()
                .statusCode(204).extract()

        def refreshToken = response.cookie("refreshToken")

        assert "" == refreshToken

    }


}
