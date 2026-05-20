package com.stackly.standup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stackly.common.entity.Teams;




public interface TeamsRepository extends JpaRepository<Teams, Long> {

}
