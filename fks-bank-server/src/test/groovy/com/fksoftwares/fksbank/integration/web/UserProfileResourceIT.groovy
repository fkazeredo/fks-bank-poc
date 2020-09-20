package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.core.file.FileUploader
import com.fksoftwares.fksbank.integration.UserRequestFactory
import com.fksoftwares.fksbank.userprofile.Permission
import com.fksoftwares.fksbank.userprofile.UserProfile
import com.fksoftwares.fksbank.userprofile.UserProfileMailer
import com.fksoftwares.fksbank.userprofile.UserProfileRepository
import com.fksoftwares.fksbank.userprofile.web.input.*
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileModel
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
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.test.context.ActiveProfiles

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserProfileResourceIT {

    private static final String BASE_URL = "/user-profiles"

    @LocalServerPort
    Integer port

    @Autowired
    private UserProfileRepository userProfileRepository

    @Autowired
    private MessageSource messageSource

    @MockBean
    private UserProfileMailer userProfileMailer

    @MockBean
    private FileUploader fileUploader

    @Autowired
    private Flyway flyway

    private Faker faker = new Faker()

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN a manager user profile, WHEN search by filter, THEN  should return '200' success response and a paginated list of user profiles"() {

        Map<String, Object> filter = new HashMap<>()
        filter.put("permission", Permission.MANAGER)
        filter.put("mail", "admin")

        List<Map<String, Object>> content = UserRequestFactory.withManager()
                .queryParams(filter)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200).extract().body().jsonPath().get("content")

        assert content != null
        assert 3 == content.size()

        content.forEach({ item ->
            assert (item.get("username") as String).contains("admin")
        })

    }

    @Test
    void "GIVEN a customer user profile, WHEN search by filter, THEN should return '403' error response"() {

        Map<String, Object> filter = new HashMap<>()
        filter.put("permission", Permission.MANAGER)
        filter.put("mail", "admin")

        UserRequestFactory.withCustomer()
                .queryParams(filter)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a manager, WHEN find by another user profile id, THEN should return '200' success response and user profile model"() {

        def id = 2L

        def userProfileModel = UserRequestFactory.withManager()
                .when()
                .get(BASE_URL + "/" + id)
                .then()
                .statusCode(200).extract().as(UserProfileModel)

        assert userProfileModel != null
        assert id == userProfileModel.id

    }

    @Test
    void "GIVEN a customer, WHEN find by his own id, THEN should return '200' success response and his own user profile model"() {

        def id = 2L

        def userProfileModel = UserRequestFactory.withCustomer()
                .when()
                .get(BASE_URL + "/" + id)
                .then()
                .statusCode(200).extract().as(UserProfileModel)

        assert userProfileModel != null
        assert id == userProfileModel.id

    }

    @Test
    @DisplayName("GIVEN a customer, WHEN find by another user profile id, THEN should return '403' error response")
    void whenACustomerFindByAnotherUserProfileId_ThenShouldReturn403ErrorResponse() {

        def id = 1L

        UserRequestFactory.withCustomer()
                .when()
                .get(BASE_URL + "/" + id)
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a manager, WHEN creates a manager user profile, THEN should return '200' success response and user profile model"() {

        def input = new CreateManagerUserProfileInput(
                cpf: "62310531057",
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                phone: faker.number().digits(10),
                username: faker.internet().emailAddress(),
                address: new AddressInput(
                        zipCode: "20250120",
                        street: faker.address().streetName(),
                        number: faker.number().digits(3),
                        complement: faker.lorem().characters(20),
                        neighborhood: faker.lorem().characters(25),
                        city: faker.address().cityName()
                )
        )

        def userProfileModel = UserRequestFactory.withManager()
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201).extract().as(UserProfileModel)

        verify(userProfileMailer, times(1)).sendGreetings(any(UserProfile))

        assert userProfileModel != null
        assert input.cpf == userProfileModel.cpf

    }

    @Test
    void "GIVEN a customer, WHEN creates a manager, THEN should return '403' error response"() {

        def input = new CreateManagerUserProfileInput(
                cpf: "62310531057",
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                phone: faker.number().digits(10),
                username: faker.internet().emailAddress(),
                address: new AddressInput(
                        zipCode: "20250120",
                        street: faker.address().streetName(),
                        number: faker.number().digits(3),
                        complement: faker.lorem().characters(20),
                        neighborhood: faker.lorem().characters(25),
                        city: faker.address().cityName()
                )
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN an user, WHEN changes his own personal info, THEN should return '200' success response and user profile model"() {

        def id = 2L
        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                phone: faker.number().digits(10),
                mail: faker.internet().emailAddress(),
                address: new AddressInput(
                        zipCode: "20250120",
                        street: faker.address().streetName(),
                        number: faker.number().digits(3),
                        complement: faker.lorem().characters(20),
                        neighborhood: faker.lorem().characters(25),
                        city: faker.address().cityName()
                )
        )

        def userProfileModel = UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/personal-info")
                .then()
                .statusCode(200).extract().as(UserProfileModel)

        assert userProfileModel != null
        assert id == userProfileModel.id

    }

    @Test
    void "GIVEN an user, WHEN changes another user personal info, THEN should return '403' error response"() {

        def id = 2L
        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: faker.name().firstName(),
                lastName: faker.name().lastName(),
                phone: faker.number().digits(10),
                mail: faker.internet().emailAddress(),
                address: new AddressInput(
                        zipCode: "20250120",
                        street: faker.address().streetName(),
                        number: faker.number().digits(3),
                        complement: faker.lorem().characters(20),
                        neighborhood: faker.lorem().characters(25),
                        city: faker.address().cityName()
                )
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/personal-info")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN an user, WHEN changes his own profile picture, THEN should return '204' success response"() throws IOException {

        Resource resource = new ClassPathResource("test.png")
        def id = 2L

        UserRequestFactory.withCustomerWithMultiform()
                .multiPart(resource.file)
                .when()
                .patch(BASE_URL + "/" + id + "/picture")
                .then()
                .statusCode(204)
    }

    @Test
    void "GIVEN an user, WHEN changes another user picture, THEN should return '403' error response"() throws IOException {

        Resource resource = new ClassPathResource("test.png")
        def id = 1L

        UserRequestFactory.withCustomerWithMultiform()
                .multiPart(resource.file)
                .when()
                .patch(BASE_URL + "/" + id + "/picture")
                .then()
                .statusCode(403)
    }

    @Test
    void "GIVEN an user, WHEN changes his own password, THEN should return '204' success response"() throws IOException {

        def id = 2L
        def input = new ChangeUserProfilePasswordInput(
                password: "Abc@1234",
                passwordConfirmation: "Abc@1234"
        )

       UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/password")
                .then()
                .statusCode(204)
    }

    @Test
    void "GIVEN an user, WHEN changes another user profile password, THEN should return '403' error response"() throws IOException {

        def id = 1L
        def input = new ChangeUserProfilePasswordInput(
                password: "Abc@1234",
                passwordConfirmation: "Abc@1234"
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/password")
                .then()
                .statusCode(403)
    }

    @Test
    void "GIVEN a manager, WHEN approves a pending customer, THEN should return '204' success response"() throws IOException {

        def id = 7L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/approval")
                .then()
                .statusCode(204)
    }

    @Test
    void "GIVEN a manager, WHEN approves a napproved customer, THEN should return '400' error response"() throws IOException {

        def id = 2L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/approval")
                .then()
                .statusCode(400)
    }

    @Test
    void "GIVEN a manager, WHEN approves a rejected customer, THEN should return '400' error response"() throws IOException {

        def id = 8L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/approval")
                .then()
                .statusCode(400)
    }

    @Test
    void "GIVEN a customer, WHEN approves another customer, THEN should return '403' error response"() throws IOException {

        def id = 2L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/approval")
                .then()
                .statusCode(403)
    }

    @Test
    void "GIVEN a manager, WHEN rejects a pending customer, THEN should return '204' success response"() throws IOException {

        def id = 7L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .when()
                .patch(BASE_URL + "/" + id + "/rejection")
                .then()
                .statusCode(204)
    }

    @Test
    void "GIVEN a manager, WHEN rejects an approved customer, THEN should return '400' error response"() throws IOException {

        def id = 2L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .when()
                .patch(BASE_URL + "/" + id + "/rejection")
                .then()
                .statusCode(400)
    }

    @Test
    void "GIVEN a manager, WHEN rejects an rejected customer, THEN should return '400' error response"() throws IOException {

        def id = 8L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .when()
                .patch(BASE_URL + "/" + id + "/rejection")
                .then()
                .statusCode(400)
    }

    @Test
    void "GIVEN a customer, WHEN rejects another customer, THEN should return '403' error response"() throws IOException {

        def id = 7L
        def input = new ApproveUserProfileInput(
                maxLimit: new BigDecimal("2000.")
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/approval")
                .then()
                .statusCode(204)

        UserRequestFactory.withCustomer()
                .when()
                .patch(BASE_URL + "/" + id + "/rejection")
                .then()
                .statusCode(403)
    }

    @Test
    void "GIVEN a manager, WHEN changes an user profile enablement, THEN should return '204' success response"() throws IOException {

        def id = 2L
        def input = new ChangeUserProfileEnablementInput(
                enablement: Boolean.FALSE
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/enablement")
                .then()
                .statusCode(204)
    }

    @Test
    void "GIVEN a customer, WHEN changes an user profile enablement, THEN should return '403' error response"() throws IOException {

        def id = 2L
        def input = new ChangeUserProfileEnablementInput(
                enablement: Boolean.FALSE
        )

       UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch(BASE_URL + "/" + id + "/enablement")
                .then()
                .statusCode(403)
    }


}
