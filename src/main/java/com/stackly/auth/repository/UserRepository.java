package com.stackly.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackly.common.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query to find a user by email for Login/Signup checks
    Optional<User> findByEmail(String email);

    // Useful for Signup to check if an email is already taken
    Boolean existsByUsername(String username);

}