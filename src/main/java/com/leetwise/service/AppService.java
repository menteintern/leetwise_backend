package com.leetwise.service;

import org.springframework.http.ResponseEntity;

import com.leetwise.model.CodeExecutionRequest;

public interface  AppService {
	
	ResponseEntity<?> getUserProfile(String username);
	
	ResponseEntity<?> getQuestionData(String titleSlug);
	
	ResponseEntity<?> executeCode(CodeExecutionRequest request);
	
	ResponseEntity<?> getContestData(String titleslug);
	
	ResponseEntity<?> getSolution(String titleSlug);
}
