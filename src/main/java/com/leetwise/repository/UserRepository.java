package com.leetwise.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leetwise.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}