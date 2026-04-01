package com.Cinema.App.service;

import com.Cinema.App.exception.InformationNotFoundException;
import com.Cinema.App.model.Movie;
import com.Cinema.App.repository.MovieRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private Cloudinary cloudinary;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Movie not found with id: " + id));
    }

    public List<Movie> searchByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> getByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }

    public Movie createMovie(Movie movie, MultipartFile poster) throws IOException {
        if (poster != null && !poster.isEmpty()) {
            movie.setPosterUrl(uploadImage(poster));
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie updated, MultipartFile poster) throws IOException {
        Movie movie = getMovieById(id);
        movie.setTitle(updated.getTitle());
        movie.setDescription(updated.getDescription());
        movie.setDurationMinutes(updated.getDurationMinutes());
        movie.setGenre(updated.getGenre());
        movie.setUpdatedAt(LocalDateTime.now());
        if (poster != null && !poster.isEmpty()) {
            movie.setPosterUrl(uploadImage(poster));
        }
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private String uploadImage(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("Unsupported image type. Allowed: JPEG, PNG, WEBP, GIF");
        }
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "cinema/posters"));
        return (String) uploadResult.get("secure_url");
    }
}
