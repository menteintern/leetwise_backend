package com.leetwise.repository;

import com.leetwise.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{

	Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}