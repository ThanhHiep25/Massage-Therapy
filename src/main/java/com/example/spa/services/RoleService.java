package com.example.spa.services;

import com.example.spa.dto.request.RoleRequest;
import com.example.spa.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role findByName(String roleName);

    Optional<Role> findById(Long id);

    // Thêm role mới
    Role save(RoleRequest role);

    void deleteById(Long id);

    Role update(Role role);

    Role getById(Long id);

    List<Role> getAllRoles();

}
