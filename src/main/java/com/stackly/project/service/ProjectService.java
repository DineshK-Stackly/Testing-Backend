package com.stackly.project.service;

import java.util.List;
import java.util.Map;

import com.stackly.common.dto.ProjectRequestDto;
import com.stackly.common.dto.ProjectResponseDto;
import com.stackly.common.entity.MasReference;

public interface ProjectService {

	ProjectResponseDto createProject(ProjectRequestDto dto);

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto updateProject(Long id, ProjectRequestDto dto);

    void deleteProject(Long id);
    
    public List<Map<String, Object>> getProjectDashboard(String role,Long memberId,String sortType,String projectStatus);
    
    List<Map<String, Object>> getProjectTasks(Long projectId, String status);
    
    List<MasReference> getReferenceNames(List<String> names);

}
