package com.Cinema.App.controller;

import com.Cinema.App.model.Movie;
import com.Cinema.App.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/search")
    public List<Movie> search(@RequestParam String title) {
        return movieService.searchByTitle(title);
    }

    @GetMapping("/genre/{genre}")
    public List<Movie> byGenre(@PathVariable String genre) {
        return movieService.getByGenre(genre);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> createMovie(
            @RequestPart("movie") Movie movie,
            @RequestPart(value = "poster", required = false) MultipartFile poster) throws IOException {
        return ResponseEntity.ok(movieService.createMovie(movie, poster));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @RequestPart("movie") Movie movie,
            @RequestPart(value = "poster", required = false) MultipartFile poster) throws IOException {
        return ResponseEntity.ok(movieService.updateMovie(id, movie, poster));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Movie deleted successfully");
    }
}
