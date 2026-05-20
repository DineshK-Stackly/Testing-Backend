package com.stackly.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stackly.common.dto.ProjectRequestDto;
import com.stackly.common.dto.ProjectResponseDto;
import com.stackly.common.entity.MasReference;
import com.stackly.common.util.AppConstant;
import com.stackly.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestBody ProjectRequestDto request) {

    	ProjectResponseDto savedProject = projectService.createProject(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Your project has been saved successfully");
        response.put("data", savedProject);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {

        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequestDto dto) {

        return ResponseEntity.ok(
                projectService.updateProject(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.ok("Project deleted successfully");
    }
    
    @PostMapping("/getProjectCardDetails")
    public ResponseEntity<List<ProjectResponseDto>> getProjectCardsDetails(@RequestBody ProjectRequestDto dto) {

    	 return ResponseEntity.ok(projectService.getAllProjects());
    }
    @GetMapping("/dashboardDetails")
    public ResponseEntity<?> getProjectDashboard(
            @RequestParam String role,
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "RECENT") String sortType,
            @RequestParam(required = false) String projectStatus
    ) {

        return ResponseEntity.ok(projectService.getProjectDashboard(role,memberId,sortType,projectStatus));
    }

    @GetMapping("/memberDashboardDetails")
    public ResponseEntity<?> getMemberProjectDashboard(
    		@RequestParam Long projectId,
            @RequestParam(required = false) String status
    ) {

        return ResponseEntity.ok(
                projectService.getProjectTasks(projectId, status)
        );
    }
    @GetMapping("/references")
    public List<MasReference> getReferences() {

        return projectService.getReferenceNames(AppConstant.PROJECT_REFERENCE_NAMES);
    }
    
}
