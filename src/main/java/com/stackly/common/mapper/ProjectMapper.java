package com.stackly.common.mapper;

import java.time.LocalDateTime;

import com.stackly.common.dto.ProjectRequestDto;
import com.stackly.common.dto.ProjectResponseDto;
import com.stackly.common.entity.Project;

public class ProjectMapper {

    public static Project toEntity(ProjectRequestDto dto) {

        return Project.builder()
                .projectName(dto.getProjectName())
                .projectDescription(dto.getProjectDescription())
                .projectStatus(dto.getProjectStatus())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createdBy(dto.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ProjectResponseDto toDto(Project project) {

        return ProjectResponseDto.builder()
                .id(project.getProjectId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .projectStatus(project.getProjectStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdBy(project.getCreatedBy())
                .createdAt(project.getCreatedAt())
                .build();
    }
}

