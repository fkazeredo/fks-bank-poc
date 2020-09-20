package com.fksoftwares.fksbank.userprofile

import com.fksoftwares.fksbank.core.BusinessException
import com.fksoftwares.fksbank.core.ConcurrencySafeEntity
import com.fksoftwares.fksbank.core.EntityIsDisabledException
import org.hibernate.validator.constraints.br.CPF

import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class UserProfile extends ConcurrencySafeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @CPF
    @NotBlank
    private String cpf

    @Email
    @NotBlank
    @Size(min = 1, max = 150)
    private String username

    @NotNull
    @Valid
    @Embedded
    private Name name

    @NotNull
    @Valid
    @Embedded
    private Contact contact

    @Size(min = 1, max = 255)
    private String pictureUrl

    @Valid
    @Embedded
    private Address address

    @Valid
    @Embedded
    private Password password

    @Valid
    @Embedded
    private PasswordRecoveryToken passwordRecoveryToken

    @Enumerated(EnumType.STRING)
    @NotNull
    private Permission permission

    private Boolean enabled

    @Enumerated(EnumType.STRING)
    private UserProfileStatus status

    UserProfile(String cpf, Name name, Contact contact) {
        this.cpf = cpf
        this.username = contact.getMail()
        this.name = name
        this.contact = contact
        this.permission = Permission.CUSTOMER
        this.enabled = Boolean.FALSE
        this.status = UserProfileStatus.PENDING
    }

    UserProfile(String cpf, Name name, Contact contact, Address address) {
        this.cpf = cpf
        this.username = contact.getMail()
        this.name = name
        this.contact = contact
        this.address = address
        this.permission = Permission.MANAGER
        this.enabled = Boolean.FALSE
        this.status = UserProfileStatus.APPROVED
    }

    void changePersonalInfo(Name name, Contact contact, Address address) {
        this.validateEnablement()
        this.name = name
        this.contact = contact
        this.username = contact.getMail()
        this.address = address
    }

    void changePictureUrl(String pictureUrl) {
        this.validateEnablement()
        this.pictureUrl = pictureUrl
    }

    void changePassword(Password password) {
        this.validateEnablement()
        this.password = password
    }

    void changePasswordByRecoveryToken(Password password) {
        this.validatePasswordRecoveryToken()
        this.password = password
        this.enabled = Boolean.TRUE
        this.cleanPasswordRecoveryToken()
    }

    void reject() {
        if (this.status != UserProfileStatus.PENDING)
            throw new BusinessException("userIsNotPending")
        this.status = UserProfileStatus.REJECTED
    }

    void approve() {
        if (this.status != UserProfileStatus.PENDING)
            throw new BusinessException("userIsNotPending")
        this.generatePasswordRecoveryToken()
    }

    void generatePasswordRecoveryToken() {
        this.passwordRecoveryToken = new PasswordRecoveryToken(UUID.randomUUID().toString())
    }

    void cleanPasswordRecoveryToken() {
        this.passwordRecoveryToken = null
    }

    void enable() {
        if (!this.enabled)
            this.enabled = Boolean.TRUE
    }

    void disable() {
        if (this.enabled)
            this.enabled = Boolean.FALSE
    }

    void validatePasswordRecoveryToken() {
        if (passwordRecoveryToken == null || !passwordRecoveryToken.isValid())
            throw new PasswordRecoveryTokenExpiredException("userProfilePasswordRecoveryTokenExpired")
    }

    private void validateEnablement() {
        if (!this.enabled)
            throw new EntityIsDisabledException("userProfileDisabled", String.valueOf(this.id))
    }

    Boolean isValid() {
        return this.enabled && status == UserProfileStatus.APPROVED
    }

    Long getId() {
        return id
    }

    String getCpf() {
        return cpf
    }

    String getUsername() {
        return username
    }

    Name getName() {
        return name
    }

    Contact getContact() {
        return contact
    }

    String getPictureUrl() {
        return pictureUrl
    }

    Address getAddress() {
        return address
    }

    Password getPassword() {
        return password
    }

    PasswordRecoveryToken getPasswordRecoveryToken() {
        return passwordRecoveryToken
    }

    Permission getPermission() {
        return permission
    }

    Boolean getEnabled() {
        return enabled
    }

    UserProfileStatus getStatus() {
        return status
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof UserProfile)) return false

        UserProfile that = (UserProfile) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

    // JPA requirement
    protected UserProfile() {}

    // Unit tests requirement
    protected void setId(Long id) {
        this.id = id
    }

    protected void setStatus(UserProfileStatus status) {
        this.status = status
    }

}
