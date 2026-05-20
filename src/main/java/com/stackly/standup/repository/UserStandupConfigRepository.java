package com.stackly.standup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackly.common.entity.UserStandupConfig;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserStandupConfigRepository extends JpaRepository<UserStandupConfig, Long> {
    Optional<UserStandupConfig> findByUserId(String userId);

    List<UserStandupConfig> findByActiveTrueAndScheduledTime(LocalTime scheduledTime);
    List<UserStandupConfig> findByActiveTrue();

//	UserStandupConfig findByAadUserId(String aadUserId);

//	List<UserStandupConfig> findActiveConfigs();

}
