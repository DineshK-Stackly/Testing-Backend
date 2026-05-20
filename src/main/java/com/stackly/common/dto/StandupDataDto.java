package com.stackly.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandupDataDto {
	private Long teamId;
	private String userId;   
	private String userName;
	private String question;
	private String answer;
	private LocalDateTime date;
}
