package com.fksoftwares.fksbank.userprofile

import org.springframework.data.repository.Repository

interface UserProfileRepository extends Repository<UserProfile, Long>, UserProfileQueryRepository {

    UserProfile save(UserProfile userProfile)
    Optional<UserProfile> findById(Long id)
    Optional<UserProfile> findByPasswordRecoveryTokenValue(String passwordRecoveryToken)
    Optional<UserProfile> findByUsername(String username)
    
}
