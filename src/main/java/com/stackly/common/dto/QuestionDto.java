package com.stackly.common.dto;

import lombok.Data;

@Data
public class QuestionDto {
	private Long questionId;
	private String questionText;
    private Long questionOrderSeq;
    private String answer;
}
