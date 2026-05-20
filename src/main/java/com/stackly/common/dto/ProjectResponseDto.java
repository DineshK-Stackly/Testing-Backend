package com.stackly.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseDto {

    private Long id;

    private String projectName;

    private String projectDescription;

    private String projectStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long createdBy;

    private LocalDateTime createdAt;
}
