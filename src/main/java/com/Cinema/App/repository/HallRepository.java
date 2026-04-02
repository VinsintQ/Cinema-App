package com.Cinema.App.repository;

import com.Cinema.App.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {
    Optional<Hall> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
