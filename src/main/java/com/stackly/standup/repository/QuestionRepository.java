package com.stackly.standup.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackly.common.entity.StandupQuestion;
import com.stackly.common.entity.Teams;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<StandupQuestion, Long> {

    List<StandupQuestion> findByActiveTrue();
    Optional<StandupQuestion> findByTeam_TeamIdAndQuestionOrderSeqAndActiveTrue(Teams team, Long order);
    List<StandupQuestion> findByActiveTrueOrderByStandupQuestionIdAsc();
    List<StandupQuestion> findByTeam_TeamIdAndActiveTrueOrderByQuestionOrderSeqAsc(Long teamId);
    Optional<StandupQuestion> findById(Long id);
//	StandupQuestion findByOrder(int questionOrder);
}
