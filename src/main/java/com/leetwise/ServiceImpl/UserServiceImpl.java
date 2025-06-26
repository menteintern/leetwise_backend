package com.leetwise.ServiceImpl;

import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.leetwise.Entity.User;
import com.leetwise.repository.UserRepository;
import com.leetwise.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
    private UserRepository userRepository;
	
	@Override
	public ResponseEntity<?> createUser(User user) {
		Optional<User> isUserExist = userRepository.findByEmail(user.getEmail());
		if(isUserExist.isPresent()) {
			logger.info("user is already present");
			return ResponseEntity.ok("User already hai!");
		}
		else {
			userRepository.save(user);
			
		}
		return ResponseEntity.ok("Kar dia add!");
	}
}
