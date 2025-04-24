package com.example.spa.repositories;

import com.example.spa.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByName(String name);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    Optional<Staff> findByEmailOrName(String identifier, String identifier1);

}
