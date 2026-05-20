package com.stackly.common.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ProjectRequestDto {

	private String projectName;
	private String projectDescription;
	private String projectStatus;
	private String projectCategory;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long createdBy;
	private String Priority;
	private String ProjectColor;
	private Double Budget;
	private LocalDate Deadline;
	private List<String> members;
}

