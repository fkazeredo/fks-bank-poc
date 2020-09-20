package com.fksoftwares.fksbank.integration

import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

import static org.junit.jupiter.api.Assertions.assertNotNull

class UserRequestFactory {

    private static final String BASE_URL = "/oauth/token"

    static Response loginManager() {
        return RestAssured.given()
                .auth().basic("fkbank_ui", "Ab1234@")
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("application/x-www-form-urlencoded charset=UTF-8", ContentType.URLENC)))
                .formParam("client", "fkbank_ui")
                .formParam("username", "admin@admin.com")
                .formParam("password", "Ab1234@")
                .formParam("grant_type", "password")
                .when()
                .post(BASE_URL)
    }

    static Response loginCustomer() {
        return RestAssured.given()
                .auth().basic("fkbank_ui", "Ab1234@")
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("application/x-www-form-urlencoded charset=UTF-8", ContentType.URLENC)))
                .formParam("client", "fkbank_ui")
                .formParam("username", "customer@customer.com")
                .formParam("password", "Ab1234@")
                .formParam("grant_type", "password")
                .when()
                .post(BASE_URL)
    }

    static RequestSpecification withManager() {
        ExtractableResponse<Response> response = loginManager()
                .then()
                .extract()

        assertNotNull(response)
        def accessToken = response.body().jsonPath().getString("access_token")

        return RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
    }

    static RequestSpecification withCustomer() {
        ExtractableResponse<Response> response = loginCustomer()
                .then()
                .extract()

        assertNotNull(response)
        def accessToken = response.body().jsonPath().getString("access_token")

        return RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
    }

    static RequestSpecification withAnonymous() {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
    }

    static RequestSpecification withCustomerWithMultiform() {
        ExtractableResponse<Response> response = loginCustomer()
                .then()
                .extract()

        assertNotNull(response)
        def accessToken = response.body().jsonPath().getString("access_token")

        return RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
    }

}
