package com.Cinema.App.repository;


import com.Cinema.App.model.Role;
import com.Cinema.App.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(RoleName name);
    Optional<Role> findByName(RoleName name);
}
