package com.leetwise.service;

import org.springframework.http.ResponseEntity;

import com.leetwise.Entity.User;

public interface UserService {

	ResponseEntity<?> createUser(User user);
	
}
