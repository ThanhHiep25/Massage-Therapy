package com.example.spa.servicesImpl;

import com.example.spa.entities.Role;
import com.example.spa.repositories.RoleRepository;
import com.example.spa.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

}
