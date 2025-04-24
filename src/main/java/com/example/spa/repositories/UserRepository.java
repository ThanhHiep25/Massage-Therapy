package com.example.spa.repositories;

import com.example.spa.entities.User;
import com.example.spa.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String identifier, String identifier1);

    List<User> findByStatus(UserStatus status); // Lọc user theo trạng thái

    List<User> findAllByRoleRoleName(String roleName);
}


