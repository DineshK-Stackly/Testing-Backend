package com.stackly.project.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stackly.common.dto.ProjectRequestDto;
import com.stackly.common.dto.ProjectResponseDto;
import com.stackly.common.entity.MasReference;
import com.stackly.common.entity.Project;
import com.stackly.common.exception.CustomException;
import com.stackly.common.exception.ResourceNotFoundException;
import com.stackly.common.mapper.ProjectMapper;
import com.stackly.common.repository.MasReferenceRepository;
import com.stackly.project.repository.ProjectRepository;
import com.stackly.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    
    private final JdbcTemplate jdbcTemplate;
    
    private final MasReferenceRepository  masReferencerepository;

    @Override
    public ProjectResponseDto createProject(ProjectRequestDto request) {
    	if (request.getProjectName() == null ||
                request.getProjectName().trim().isEmpty()) {
            throw new CustomException("Project name is required");
        }

        if (request.getProjectCategory() == null || 
                request.getProjectCategory().trim().isEmpty()){
            throw new CustomException("Category is required");
        }

        if (request.getPriority() == null ||
        		request.getPriority().trim().isEmpty()){
            throw new CustomException("Priority is required");
        }

        if (request.getProjectStatus() == null ||
        		request.getProjectStatus().trim().isEmpty()){
            throw new CustomException("Status is required");
        }

        if (request.getBudget() <= 0 ||
        		request.getBudget().toString().isEmpty()){
            throw new CustomException("Budget cannot be negative");
        }

        if (request.getDeadline() == null ||
        		request.getDeadline().toString().isEmpty()){
            throw new CustomException("Deadline is required");
        }
        Project project = ProjectMapper.toEntity(request);

        Project savedProject = projectRepository.save(project);

        return ProjectMapper.toDto(savedProject);
    }

    @Override
    public List<ProjectResponseDto> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(ProjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        return ProjectMapper.toDto(project);
    }

    @Override
    public ProjectResponseDto updateProject(Long id,
                                            ProjectRequestDto dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setProjectName(dto.getProjectName());
        project.setProjectDescription(dto.getProjectDescription());
        project.setProjectStatus(dto.getProjectStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setCreatedBy(dto.getCreatedBy());

        Project updatedProject = projectRepository.save(project);

        return ProjectMapper.toDto(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        projectRepository.delete(project);
    }
    
    public List<Map<String, Object>> getProjectDashboard(String role,Long memberId,String sortType,String projectStatus){

        String sql = "SELECT * FROM get_project_dashboard(?, ?, ?, ?)";

        return jdbcTemplate.queryForList(sql,role,memberId,sortType,projectStatus);
    }
    public List<Map<String, Object>> getProjectTasks(Long projectId, String status) {
    	
    	  String sql = "SELECT * FROM fn_get_member_project_tasks(?, ?)";

          return jdbcTemplate.queryForList(sql,projectId,status);
    }
    
    @SuppressWarnings("unchecked")
	public List<MasReference> getReferenceNames(List<String> names) {

        return (List<MasReference>) masReferencerepository.findByReferenceNameIn(names)
        					.orElseThrow(() ->
                        new CustomException(
                                "Reference not found"));
    }
}
