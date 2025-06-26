package com.leetwise.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leetwise.Entity.User;
import com.leetwise.service.UserService;

@RestController
@RequestMapping("/app")
public class AppController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	
	@Autowired
	UserService userService;
	
	@PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
		return userService.createUser(user);
    }
}
