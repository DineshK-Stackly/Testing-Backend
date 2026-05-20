package com.stackly.standup.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stackly.common.entity.StandupSession;


@Repository
public interface SessionRepository extends JpaRepository<StandupSession, Long> {

    @Query ("SELECT s FROM StandupSession s WHERE s.userId = :userId AND s.completed = false")
    StandupSession findActiveSession(@Param("userId") String userId);

    StandupSession findByUserIdAndSessionDate(String userId, LocalDate date);

	StandupSession findLatestByUserId(String userId);
}