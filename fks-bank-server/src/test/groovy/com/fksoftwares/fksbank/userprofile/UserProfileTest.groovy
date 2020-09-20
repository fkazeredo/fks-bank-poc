package com.fksoftwares.fksbank.userprofile

import com.fksoftwares.fksbank.core.BusinessException
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.assertThrows

class UserProfileTest {

    private Faker faker = new Faker()
    private String cpf
    private Name name
    private Contact contact
    private Address address

    @BeforeEach
    void setUp() {
        cpf = "42452840050"
        name = new Name(faker.name().firstName(), faker.name().lastName())
        contact = new Contact(faker.internet().emailAddress(), faker.number().digits(10))
        address = new Address(
                faker.number().digits(8),
                faker.address().streetName(),
                faker.number().digits(2),
                faker.lorem().fixedString(50),
                faker.lorem().fixedString(50),
                faker.address().cityName()
        )
    }

    @Test
    void "WHEN create a manager user profile, THEN should assign all constructors parameters to attributes, permission should be MANAGER and enabled should be false"() {

        def userProfile = new UserProfile(cpf, name, contact, address)

        assert cpf == userProfile.cpf
        assert name == userProfile.name
        assert contact == userProfile.contact
        assert address == userProfile.address
        assert Permission.MANAGER == userProfile.permission
        assert Boolean.FALSE == userProfile.enabled

    }

    @Test
    void "WHEN create a customer user profile, THEN should assign all constructors parameters to attributes, permission should be CUSTOMER and enabled should be false"() {

        def userProfile = new UserProfile(cpf, name, contact)

        assert cpf == userProfile.cpf
        assert name == userProfile.name
        assert contact == userProfile.contact
        assert Permission.CUSTOMER == userProfile.permission
        assert Boolean.FALSE == userProfile.enabled

    }

    @Test
    void "WHEN change an user profile personal info and enablement is valid, THEN should change name, contact and address values"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        userProfile.changePersonalInfo(
                new Name(faker.name().firstName(), faker.name().lastName()),
                new Contact(faker.internet().emailAddress(), faker.number().digits(10)),
                new Address(
                        faker.number().digits(8),
                        faker.address().streetName(),
                        faker.number().digits(2),
                        faker.lorem().fixedString(50),
                        faker.lorem().fixedString(50),
                        faker.address().cityName()
                )
        )

        assert name != userProfile.name
        assert contact != userProfile.contact
        assert address != userProfile.address
        assert name.fullName != userProfile.name.fullName
        assert address.fullAddress != userProfile.address.fullAddress

    }

    @Test
    void "WHEN change an user profile personal info and enablement is invalid, THEN should throws an exception"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.FALSE

        assertThrows(BusinessException, { ->

            userProfile.changePersonalInfo(
                    new Name(faker.name().firstName(), faker.name().lastName()),
                    new Contact(faker.internet().emailAddress(), faker.number().digits(10)),
                    new Address(
                            faker.number().digits(8),
                            faker.address().streetName(),
                            faker.number().digits(2),
                            faker.lorem().fixedString(50),
                            faker.lorem().fixedString(50),
                            faker.address().cityName()
                    )
            )

        })

    }

    @Test
    void "WHEN change an user profile picture url and enablement is valid, THEN should change picture url value"() {

        def pictureUrl = "/some/path"

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        assert userProfile.pictureUrl == null

        userProfile.changePictureUrl(pictureUrl)

        assert userProfile.pictureUrl != null
    }

    @Test
    void "WHEN change an user profile picture url and enablement is invalid, THEN should throws an exception"() {

        def pictureUrl = "/some/path"

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.FALSE

        assert userProfile.pictureUrl == null

        assertThrows(BusinessException, { ->

            userProfile.changePictureUrl(pictureUrl)

        })

    }

    @Test
    void "WHEN change an user profile password and enablement is valid, THEN should change password value"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        def password = new Password("Abcd@123", "Abcd@123",
                [userProfile.name.firstName, userProfile.name.lastName] as String[])

        userProfile.changePassword(password)

        assert new BCryptPasswordEncoder().matches("Abcd@123", userProfile.password.value)

    }

    @Test
    void "WHEN change an user profile password and enablement is invalid, THEN should throws an exception"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.FALSE

        def password = new Password("Abcd@123", "Abcd@123",
                [userProfile.name.firstName, userProfile.name.lastName] as String[])

        assertThrows(BusinessException, { ->

            userProfile.changePassword(password)

        })

    }

    @Test
    void "WHEN change an user profile password and password value has an invalid format, THEN should throws an exception"() {


        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        assertThrows(BusinessException, { ->

            def password = new Password("teste", "teste",
                    [userProfile.name.firstName, userProfile.name.lastName] as String[])

            userProfile.changePassword(password)

        })

    }

    @Test
    void "WHEN change an user profile password and password value not equals confirmation value, THEN should throws an exception"() {


        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.FALSE

        assertThrows(BusinessException, { ->

            def password = new Password("Abcd@123", "123@Abcd",
                    [userProfile.name.firstName, userProfile.name.lastName] as String[])

            userProfile.changePassword(password)

        })

    }

    @Test
    void "WHEN change an user profile password and password value contains any word on dictionary, THEN should throws an exception"() {


        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        String firstName = userProfile.name.firstName

        assertThrows(BusinessException, { ->

            def password = new Password(firstName + "@A123", firstName + "@A123",
                    [userProfile.name.firstName, userProfile.name.lastName] as String[])

            userProfile.changePassword(password)

        })

    }

    @Test
    void "WHEN change an user profile password by recovery token and token expiration is valid, THEN should change password value and clean the password recovery token"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.generatePasswordRecoveryToken()

        def password = new Password("Abcd@123", "Abcd@123",
                [userProfile.name.firstName, userProfile.name.lastName] as String[])

        userProfile.changePasswordByRecoveryToken(password)

        assert new BCryptPasswordEncoder().matches("Abcd@123", userProfile.getPassword().getValue())
        assert userProfile.getPasswordRecoveryToken() == null

    }

    @Test
    void "WHEN change an user profile password by recovery token and token is null, THEN should throws an exception"() {

        def userProfile = new UserProfile(cpf, name, contact)

        assertThrows(PasswordRecoveryTokenExpiredException, { ->

            def password = new Password("Abcd@123", "Abcd@123",
                    [userProfile.name.firstName, userProfile.name.lastName] as String[])

            userProfile.changePasswordByRecoveryToken(password)

        })

    }

    @Test
    void "WHEN change an user profile password by recovery token and token is expirated, THEN should throws an exception"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.generatePasswordRecoveryToken()
        userProfile.getPasswordRecoveryToken().setExpirationDate(LocalDateTime.now().minusHours(1))

        assertThrows(PasswordRecoveryTokenExpiredException, { ->

            def password = new Password("Abcd@123", "Abcd@123",
                    [userProfile.name.firstName, userProfile.name.lastName] as String[])

            userProfile.changePasswordByRecoveryToken(password)

        })

    }

    @Test
    "WHEN reject an user profile with status equals PENDING, THEN should change status to REJECTED"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.PENDING

        userProfile.reject()

        assert userProfile.status == UserProfileStatus.REJECTED

    }

    @Test
    "WHEN reject an user profile with status equals APPROVED, THEN should throws an exception"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.APPROVED

        assertThrows(BusinessException, { ->
            userProfile.reject()
        })

    }

    @Test
    "WHEN reject an user profile with status equals REJECTED, THEN should throws an exception"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.REJECTED

        assertThrows(BusinessException, { ->
            userProfile.reject()
        })

    }

    @Test
    "WHEN approve an user profile with status equals PENDING, THEN should change status to APPROVED"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.PENDING

        userProfile.approve()

        assert userProfile.status == UserProfileStatus.APPROVED

    }

    @Test
    "WHEN approve an user profile with status equals APPROVED, THEN should throws an exception"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.APPROVED

        assertThrows(BusinessException, { ->
            userProfile.approve()
        })

    }

    @Test
    "WHEN approve an user profile with status equals REJECTED, THEN should throws an exception"() {
        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.status = UserProfileStatus.REJECTED

        assertThrows(BusinessException, { ->
            userProfile.approve()
        })

    }


    @Test
    void "WHEN generate a password recovery token on user profile, THEN should create a password recovery token"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.generatePasswordRecoveryToken()

        assert userProfile.getPasswordRecoveryToken() != null

        def expirationMinus6Hours = userProfile.getPasswordRecoveryToken().getExpirationDate().minusHours(6)

        assert expirationMinus6Hours == userProfile.getPasswordRecoveryToken().getCreatedDate()

    }

    @Test
    void "WHEN clean a password recovery token on user profile, THEN should have no password recovery token"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.generatePasswordRecoveryToken()

        assert userProfile.getPasswordRecoveryToken() != null

        userProfile.cleanPasswordRecoveryToken()

        assert userProfile.getPasswordRecoveryToken() == null

    }

    @Test
    void "WHEN enable a disabled user profile, THEN should change enabled to true"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.FALSE

        userProfile.enable()

        assert userProfile.enabled

    }

    @Test
    void "WHEN disable an enabled user profile, THEN should change enabled to false"() {

        def userProfile = new UserProfile(cpf, name, contact)
        userProfile.enabled = Boolean.TRUE

        userProfile.disable()

        assert !userProfile.enabled

    }

    void "WHEN two user profiles have the same id, even if the other attributes are different, THEN they should be equal"() {

        Long id = 20L

        def a = new UserProfile(cpf, name, contact)
        a.setId(id)

        def b = new UserProfile(
                "32733043080",
                new Name(faker.name().firstName(), faker.name().lastName()),
                new Contact(faker.internet().emailAddress(), "2125698899")
        )
        b.setId(id)

        assert a == b

    }

}
