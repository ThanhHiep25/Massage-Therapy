package com.example.spa.services;

import com.example.spa.entities.Role;

import java.util.Optional;

public interface RoleService {
    Role findByName(String roleName);
    Optional<Role> findById(Long id);
}
