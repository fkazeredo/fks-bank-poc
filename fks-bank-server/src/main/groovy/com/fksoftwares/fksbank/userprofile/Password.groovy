package com.fksoftwares.fksbank.userprofile

import com.fksoftwares.fksbank.core.BusinessException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import javax.persistence.Column
import javax.persistence.Embeddable
import java.util.regex.Matcher
import java.util.regex.Pattern

@Embeddable
class Password {

    private static final Pattern regexPattern = ~/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$/

    @Column(name = "password")
    private String value

    Password(String aPassword, String aPasswordConfirmation, String[] aDictionary) {
        validateFormat(aPassword)
        validateConfirmation(aPassword, aPasswordConfirmation)
        validateDictionary(aPassword, aDictionary)
        this.value = new BCryptPasswordEncoder().encode(aPassword)
    }

    private void validateFormat(String aPassword) {
        Matcher matcher = regexPattern.matcher(aPassword)
        if (!matcher.matches()) {
            throw new BusinessException("passwordFormatException")
        }
    }

    private void validateConfirmation(String aPassword, String aPasswordConfirmation) {
        if (aPassword != aPasswordConfirmation)
            throw new BusinessException("passwordConfirmationException")
    }

    private void validateDictionary(String aPassword, String[] aDictionary) {
        List<String> dicionarioCompleto = new ArrayList<>()
        if (aDictionary != null) {
            for (String word : aDictionary) {
                String[] parts = word.split(" ")
                dicionarioCompleto.addAll(Arrays.asList(parts))
            }
            if (dicionarioCompleto.stream().anyMatch({ streamValue ->
                aPassword.toUpperCase().contains(streamValue.toUpperCase())
            }
            )) throw new BusinessException("weakPasswordException")
        }
    }

    String getValue() {
        return value
    }

    // JPA requirement
    protected Password() {
    }

}
