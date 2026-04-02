package com.Cinema.App.controller;

import com.Cinema.App.model.Hall;
import com.Cinema.App.model.Seat;
import com.Cinema.App.service.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    @Autowired
    private HallService hallService;

    @GetMapping
    public List<Hall> getAllHalls() {
        return hallService.getAllHalls();
    }

    @GetMapping("/{id}")
    public Hall getHall(@PathVariable Long id) {
        return hallService.getHallById(id);
    }

    @GetMapping("/{id}/seats")
    public List<Seat> getSeats(@PathVariable Long id) {
        return hallService.getSeatsForHall(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hall> createHall(@RequestBody Hall hall) {
        return ResponseEntity.ok(hallService.createHall(hall));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hall> updateHall(@PathVariable Long id, @RequestBody Hall hall) {
        return ResponseEntity.ok(hallService.updateHall(id, hall));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.ok("Hall deleted successfully");
    }
}
