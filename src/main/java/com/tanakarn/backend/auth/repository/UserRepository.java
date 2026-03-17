package com.tanakarn.backend.auth.repository;

import com.tanakarn.backend.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
