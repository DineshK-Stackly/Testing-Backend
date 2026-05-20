package com.stackly.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.stackly.common.dto.TaskDto;
import com.stackly.project.entity.Task;

public interface TaskService {

	ResponseEntity<Map<String, Object>> createTask(TaskDto taskDto);

	ResponseEntity<List<Task>> getAllTasks();

	ResponseEntity<Map<String, Object>> updateTask(Long id, TaskDto taskDto);

	ResponseEntity<Map<String, Object>> deleteTask(Long id);
}
