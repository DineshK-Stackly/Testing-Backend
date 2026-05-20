package com.stackly.common.dto;

import java.util.List;

import lombok.Data;

@Data
public class StandupQuestionRequest {
	private Long teamId;
    private List<QuestionDto> questions;
}
