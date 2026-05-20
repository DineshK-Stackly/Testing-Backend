package com.stackly.project.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stackly.common.dto.TaskDto;
import com.stackly.common.exception.CustomException;
import com.stackly.project.entity.Task;
import com.stackly.project.repository.TaskRepository;
import com.stackly.project.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public ResponseEntity<Map<String, Object>> createTask(TaskDto taskDto) {

        Task task = new Task();

        task.setProjectId(taskDto.getProjectId());
        task.setAssigneeId(taskDto.getAssigneeId());
        task.setTaskName(taskDto.getTaskName());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setStatus(taskDto.getStatus());

        task.setCreatedBy(taskDto.getCreatedBy());
        task.setModifiedBy(taskDto.getModifiedBy());

        task.setCreatedDate(LocalDateTime.now());
        task.setModifiedDate(LocalDateTime.now());

        task.setActiveStatus("Y");

        Task savedTask = taskRepository.save(task);

        Map<String, Object> response = new HashMap<String, Object>();

        response.put("message", "Task created successfully");
        response.put("data", savedTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<Task>> getAllTasks() {

        List<Task> tasks = taskRepository.findByActiveStatus("Y");

        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateTask(Long id, TaskDto taskDto) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException("Task not found"));

        task.setProjectId(taskDto.getProjectId());
        task.setAssigneeId(taskDto.getAssigneeId());
        task.setTaskName(taskDto.getTaskName());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setStatus(taskDto.getStatus());

        task.setModifiedBy(taskDto.getModifiedBy());
        task.setModifiedDate(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        Map<String, Object> response = new HashMap<String, Object>();

        response.put("message", "Task updated successfully");
        response.put("data", updatedTask);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException("Task not found"));

        task.setActiveStatus("N");

        taskRepository.save(task);

        Map<String, Object> response = new HashMap<String, Object>();

        response.put("message", "Task deleted successfully");

        return ResponseEntity.ok(response);
    }
}
