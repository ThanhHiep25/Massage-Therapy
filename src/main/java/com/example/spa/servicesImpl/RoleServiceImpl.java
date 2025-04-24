package com.example.spa.servicesImpl;

import com.example.spa.dto.request.RoleRequest;
import com.example.spa.entities.Role;
import com.example.spa.repositories.RoleRepository;
import com.example.spa.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    // Thêm role mới
    @Override
    public Role save(RoleRequest role) {
        return roleRepository.save(Role.builder().roleName(role.getRoleName()).build());
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);

    }

    @Override
    public Role update(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
