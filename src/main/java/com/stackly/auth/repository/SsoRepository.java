package com.stackly.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackly.common.entity.SsoUser;


@Repository
public interface SsoRepository extends JpaRepository<SsoUser, Long> {

    Optional<SsoUser> findByEmail(String email);
    Optional<SsoUser> findByProviderAndProviderId(String provider, String providerId);

}