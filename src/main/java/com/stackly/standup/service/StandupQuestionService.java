package com.stackly.standup.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stackly.common.dto.ApiResponse;
import com.stackly.common.dto.QuestionDto;
import com.stackly.common.dto.StandupQuestionRequest;
import com.stackly.common.entity.StandupQuestion;
import com.stackly.common.entity.Teams;
import com.stackly.standup.repository.QuestionRepository;
import com.stackly.standup.repository.TeamsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StandupQuestionService {

    private final QuestionRepository questionRepo;
    private final TeamsRepository teamsRepo;

    public ApiResponse saveOrUpdate(StandupQuestionRequest request) {

    	try {
    		Teams team = teamsRepo.findById(request.getTeamId())
    				.orElseThrow(() -> new RuntimeException("Team not found"));

    		List<StandupQuestion> finalList = new ArrayList<>();

    		for (QuestionDto dto : request.getQuestions()) {

    			StandupQuestion question;

    			if (dto.getQuestionId() != null) {
    				question = questionRepo.findById(dto.getQuestionId())
    						.orElseThrow(() -> new RuntimeException(
    								"Question not found: " + dto.getQuestionId()));
    			}
    			else {
    				question = new StandupQuestion();
    			}

    			// Common fields
    			question.setQuestionText(dto.getQuestionText());
    			question.setQuestionOrderSeq(dto.getQuestionOrderSeq());
    			question.setTeam(team);

    			finalList.add(question);
    		}

    		questionRepo.saveAll(finalList);

    		return ApiResponse.builder()
    				.success(true)
    				.message("Questions saved/updated successfully")
    				.build();

    	} catch (Exception e) {
    		return ApiResponse.builder()
    				.success(false)
    				.message(e.getMessage())
    				.build();
    	}
    }


    public StandupQuestionRequest getQuestionsByTeam(Long teamId) {

        Teams team = teamsRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));

        List<StandupQuestion> questions = questionRepo
                .findByTeam_TeamIdAndActiveTrueOrderByQuestionOrderSeqAsc(team.getTeamId());

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for team id: " + teamId);
        }

        List<QuestionDto> questionDtos = questions.stream()
                .map(q -> {
                    QuestionDto dto = new QuestionDto();
                    dto.setQuestionId(q.getStandupQuestionId());
                    dto.setQuestionText(q.getQuestionText());
                    dto.setQuestionOrderSeq(q.getQuestionOrderSeq());
                    return dto;
                })
                .toList();

        StandupQuestionRequest response = new StandupQuestionRequest();
        response.setTeamId(teamId);
        response.setQuestions(questionDtos);

        return response;
    }

}