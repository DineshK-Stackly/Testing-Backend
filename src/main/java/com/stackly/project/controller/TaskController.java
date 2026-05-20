package com.stackly.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stackly.common.dto.TaskDto;
import com.stackly.project.entity.Task;
import com.stackly.project.service.TaskService;




@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestBody TaskDto taskDto) {

        return taskService.createTask(taskDto);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {

        return taskService.getAllTasks();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(
            @PathVariable Long id) {

        return taskService.deleteTask(id);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDto taskDto) {

        return taskService.updateTask(id, taskDto);
    }
}