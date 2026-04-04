package com.Cinema.App.controller;

import com.Cinema.App.model.Showtime;
import com.Cinema.App.model.request.ShowtimeRequest;
import com.Cinema.App.model.response.SeatAvailabilityResponse;
import com.Cinema.App.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping
    public List<Showtime> getAllShowtimes() {
        return showtimeService.getAllShowtimes();
    }

    @GetMapping("/{id}")
    public Showtime getShowtime(@PathVariable Long id) {
        return showtimeService.getShowtimeById(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatAvailabilityResponse> getSeats(@PathVariable Long id) {
        return showtimeService.getSeatsForShowtime(id);
    }

    @GetMapping("/movie/{movieId}")
    public List<Showtime> getByMovie(@PathVariable Long movieId) {
        return showtimeService.getShowtimesByMovie(movieId);
    }

    @GetMapping("/hall/{hallId}")
    public List<Showtime> getByHall(@PathVariable Long hallId) {
        return showtimeService.getShowtimesByHall(hallId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Showtime> createShowtime(@RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.createShowtime(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id, @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok("Showtime deleted successfully");
    }
}
