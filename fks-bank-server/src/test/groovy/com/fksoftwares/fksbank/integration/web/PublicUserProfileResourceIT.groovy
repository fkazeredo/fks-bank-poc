package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.integration.UserRequestFactory
import com.fksoftwares.fksbank.userprofile.UserProfile
import com.fksoftwares.fksbank.userprofile.UserProfileMailer
import com.fksoftwares.fksbank.userprofile.UserProfileRepository
import com.fksoftwares.fksbank.userprofile.web.input.ChangeUserProfilePasswordInput
import com.fksoftwares.fksbank.userprofile.web.input.CreateCustomerUserProfileInput
import com.fksoftwares.fksbank.userprofile.web.input.RequestPasswordChangeInput
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import com.github.javafaker.Faker
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.MessageSource
import org.springframework.test.context.ActiveProfiles

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PublicUserProfileResourceIT {

    private static final String BASE_URL = "/public/user-profiles"

    @LocalServerPort
    Integer port

    @Autowired
    private UserProfileRepository userProfileRepository

    @Autowired
    private MessageSource messageSource

    @MockBean
    private UserProfileMailer userProfileMailer

    private Faker faker = new Faker()

    @Autowired
    private Flyway flyway

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN an user with a not expired password recovery token, WHEN find by password recovery token, THEN should return '200' success response and user summary model"() {

        final def PASSWORD_RECOVERY_TOKEN_PATH = "/c85ed632-1d2b-43ef-ab4e-5ea49e2b0b7c"

        UserProfileSummaryModel userProfileSummary = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(BASE_URL + PASSWORD_RECOVERY_TOKEN_PATH)
                .then()
                .statusCode(200).extract().as(UserProfileSummaryModel)


        assert userProfileSummary != null
        assert 5L == userProfileSummary.id
        assert "99511159003" == userProfileSummary.cpf
        assert "Marcia" == userProfileSummary.firstName
        assert "Not Expired" == userProfileSummary.lastName
        assert "notexpired@admin.com" == userProfileSummary.username
        assert userProfileSummary.enabled

    }

    @Test
    void "GIVEN an user with an expired password recovery token, WHEN find by password recovery token, THEN should return '401' error response"() {

        final def PASSWORD_RECOVERY_TOKEN_PATH = "/cca1b723-bcfd-4ead-92b4-ac8b2340c8da"

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(BASE_URL + PASSWORD_RECOVERY_TOKEN_PATH)
                .then()
                .statusCode(401)

    }

    @Test
    void "GIVEN an user with an invalid password recovery token, WHEN find by password recovery token, THEN should return '404' error response"() {

        def invalidToken = UUID.randomUUID().toString()

        final def PASSWORD_RECOVERY_TOKEN_PATH = "/" + invalidToken

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(BASE_URL + PASSWORD_RECOVERY_TOKEN_PATH)
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN an user with status equals PENDING, WHEN find by password recovery token, THEN should return '400' error response"() {

        final def PASSWORD_RECOVERY_TOKEN_PATH = "/85de81ea-45f9-4844-a1e1-dda601b581b2"

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(BASE_URL + PASSWORD_RECOVERY_TOKEN_PATH)
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN an user with status equals REJECTED, WHEN find by password recovery token, THEN should return '400' error response"() {

        final def PASSWORD_RECOVERY_TOKEN_PATH = "/68b851a2-9558-4b26-a6ed-41b092def15a"

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(BASE_URL + PASSWORD_RECOVERY_TOKEN_PATH)
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN a not registered customer, WHEN sign up, THEN should return '204' success response and send greetings mail"() {

        def input = new CreateCustomerUserProfileInput(
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                cpf: "94215205050",
                username: faker.internet().emailAddress(),
                phone: faker.number().digits(10)
        )

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(204)

        verify(userProfileMailer, times(1)).sendGreetings(any(UserProfile))

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUsername(input.username)

        assert maybeUserProfile.isPresent()
    }

    @Test
    void "GIVEN a customer that already exists in database, WHEN sign up, THEN should return '409' error response"() {

        def input = new CreateCustomerUserProfileInput(
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                cpf: "50285826026",
                username: "customer@exists.com",
                phone: faker.number().digits(10)
        )

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(204)

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(409)

    }

    @Test
    void "GIVEN a client passing an invalid input, WHEN sign up, THEN should return '400' error response"() {

        def input = "{\"firstName\":\"John\"}"

        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN an user that forgot the password, WHEN request password change, THEN should return '204' success response and send password recovery link mail"() {

        def input = new RequestPasswordChangeInput(
                mail: "admin@admin.com"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(204)

        def maybeUserProfile = userProfileRepository.findByUsername(input.mail)

        verify(userProfileMailer, times(1)).sendPasswordRecoveryLink(any(UserProfile))

        assert maybeUserProfile.isPresent()
        assert maybeUserProfile.get().passwordRecoveryToken != null

    }

    @Test
    void "GIVEN an user with status equals PENDING, WHEN request password change, THEN should return '400' error response"() {

        def input = new RequestPasswordChangeInput(
                mail: "pending@customer.com"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN an user with status equals REJECTED, WHEN request password change, THEN should return '400' error response"() {

        def input = new RequestPasswordChangeInput(
                mail: "rejected@customer.com"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(400)

    }

    @Test
    @DisplayName("GIVEN a client who passes a mail that is not registered WHEN request password change, THEN should return '404' error response")
    void whenAClientWhoPassesAMailThatIsNotRegisteredRequestPasswordChange_ThenShouldReturn404ErrorResponse() {

        def input = new RequestPasswordChangeInput(
                mail: "notfound@user.com"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(404)

    }

    @Test
    @DisplayName("GIVEN a client passing an invalid input, WHEN request password change, THEN should return '400' error response")
    void whenAClientPassingAnInvalidInputRequestPasswordChange_ThenShouldReturn400ErrorResponse() {

        def input = new RequestPasswordChangeInput(
                mail: ""
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(400)

    }

    @Test
    @DisplayName("GIVEN an user with a valid password recovery token, WHEN change password by recovery token, THEN should return '204' success response and change the password")
    void whenAnUserWithAValidPasswordRecoveryTokenChangeThePasswordByRecoveryToken_ThenShouldReturn204SuccessResponseAndChangeThePassword() {

        def requestPasswordInput = new RequestPasswordChangeInput(
                mail: "expired@admin.com"
        )

        UserRequestFactory.withAnonymous()
                .body(requestPasswordInput)
                .when()
                .post(BASE_URL + "/password-recovery-token")
                .then()
                .statusCode(204)

        def maybeUserProfile = userProfileRepository.findByUsername(requestPasswordInput.mail)

        assert maybeUserProfile.isPresent()

        def userProfile = maybeUserProfile.get()
        def oldPassword = userProfile.password.value

        def input = new ChangeUserProfilePasswordInput(
                password: "Mnu@1234p",
                passwordConfirmation: "Mnu@1234p"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + userProfile.passwordRecoveryToken.value + "/password")
                .then()
                .statusCode(204)

        maybeUserProfile = userProfileRepository.findByUsername(requestPasswordInput.mail)
        assert maybeUserProfile.isPresent()

        userProfile = maybeUserProfile.get()
        def newPassword = userProfile.password.value

        assert oldPassword != newPassword

    }

    @Test
    void "GIVEN a client passing a password recovery token that is not registered, WHEN change password by recovery token, THEN should return '404' error response"() {

        def invalidToken = UUID.randomUUID().toString()

        def input = new ChangeUserProfilePasswordInput(
                password: "Mnu@1234p",
                passwordConfirmation: "Mnu@1234p"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + invalidToken + "/password")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a user with status equals PENDING, WHEN change password by recovery token, THEN should return '400' error response"() {

        def input = new ChangeUserProfilePasswordInput(
                password: "Mnu@1234p",
                passwordConfirmation: "Mnu@1234p"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .patch(BASE_URL + "/85de81ea-45f9-4844-a1e1-dda601b581b2/password")
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN a user with status equals REJECTED, WHEN change password by recovery token, THEN should return '400' error response"() {

        def input = new ChangeUserProfilePasswordInput(
                password: "Mnu@1234p",
                passwordConfirmation: "Mnu@1234p"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .patch(BASE_URL + "/68b851a2-9558-4b26-a6ed-41b092def15a/password")
                .then()
                .statusCode(400)

    }

    @Test
    @DisplayName("GIVEN a client passing an invalid input, WHEN change password by recovery token, THEN should return '400' error response")
    void whenAClientPassingAnInvalidInputRequestChangePasswordByRecoveryToken_ThenShouldReturn400ErrorResponse() {

        def invalidToken = UUID.randomUUID().toString()

        def input = new ChangeUserProfilePasswordInput(
                password: "",
                passwordConfirmation: "Mnu@1234p"
        )

        UserRequestFactory.withAnonymous()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + invalidToken + "/password")
                .then()
                .statusCode(400)

    }

}
