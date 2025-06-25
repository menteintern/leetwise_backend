package com.leetwise.controller;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.leetwise.model.CodeExecutionRequest;
import com.leetwise.service.AppService;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class Controller {
    
    @Autowired
    AppService appService;
    
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        return appService.getUserProfile(username);
    }
    
    @PostMapping("/question")
    public ResponseEntity<?> getLeetCodeQuestion(@RequestBody Map<String, String> request) {
        return appService.getQuestionData(request.get("titleSlug"));
    }
    
    @PostMapping("/execute-code")
    public ResponseEntity<?> executeCode(@RequestBody CodeExecutionRequest request) {
        try {
            return appService.executeCode(request);
        } catch (Exception e) {
        	logger.info("Error executing code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", e.getMessage(),
                    "details", Arrays.toString(e.getStackTrace()).substring(0, 500)
                ));
        }
    }
}
