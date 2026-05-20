package com.stackly.standup.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stackly.common.dto.StandupDataDto;
import com.stackly.common.entity.StandupResponse;

@Repository
public interface ResponseRepository extends JpaRepository<StandupResponse, Long> {
	
	@Query("""
			SELECT new com.stackly.common.dto.StandupDataDto(
			    r.teamId.id,
			    r.userId,
			    '',
			    q.questionText,
			    r.answer,
			    r.createdAt
			)
			FROM StandupResponse r
			JOIN r.question q
			WHERE r.teamId.id = :teamId
			AND r.createdAt BETWEEN :start AND :end
			ORDER BY r.createdAt, q.questionOrderSeq
			""")
			List<StandupDataDto> findTodayReport(
			        @Param("teamId") Long teamId,
			        @Param("start") LocalDateTime start,
			        @Param("end") LocalDateTime end);
}
