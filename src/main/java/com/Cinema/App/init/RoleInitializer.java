package com.Cinema.App.init;


import com.Cinema.App.model.Role;
import com.Cinema.App.model.RoleName;
import com.Cinema.App.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;



    @PostConstruct
    public void init() {


        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
        }

        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
        }
    }
}
