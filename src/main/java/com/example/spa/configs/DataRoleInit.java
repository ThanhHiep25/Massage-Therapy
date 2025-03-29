package com.example.spa.configs;

import com.example.spa.entities.Role;

import com.example.spa.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataRoleInit implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem vai trò đã tồn tại chưa, nếu chưa thì thêm vào
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("admin");
            roleRepository.save(adminRole);

            Role superadminRole = new Role();
            superadminRole.setRoleName("superadmin");
            roleRepository.save(superadminRole);

            Role customerRole = new Role();
            customerRole.setRoleName("customer");
            roleRepository.save(customerRole);

            Role staffRole = new Role();
            staffRole.setRoleName("staff");
            roleRepository.save(staffRole);

            System.out.println("Roles initialized: admin, superadmin, customer, staff");
        }
    }
}
