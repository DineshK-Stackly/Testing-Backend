package com.stackly.standup.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackly.common.dto.ApiResponse;
import com.stackly.common.dto.StandupQuestionRequest;
import com.stackly.standup.service.StandupQuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class StandupQuestionController {

    private final StandupQuestionService service;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> create(@RequestBody StandupQuestionRequest request) {
    	 ApiResponse response = service.saveOrUpdate(request);

    	    if (response.isSuccess()) {
    	        return ResponseEntity.ok(response);
    	    } else {
    	        return ResponseEntity.badRequest().body(response);
    	    }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<StandupQuestionRequest> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(service.getQuestionsByTeam(teamId));
    }
}