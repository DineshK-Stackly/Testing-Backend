package com.stackly.standup.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.stackly.common.entity.StandupResponse;


public interface StandupDashboardRepository extends Repository<StandupResponse, Long> {

	@Query(value = "SELECT get_standup_dashboard(:teamId, :date)", nativeQuery = true)
	String getDashboard(Long teamId, LocalDate date);
}
