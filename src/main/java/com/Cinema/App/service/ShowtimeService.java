package com.Cinema.App.service;

import com.Cinema.App.exception.InformationNotFoundException;
import com.Cinema.App.model.Hall;
import com.Cinema.App.model.Movie;
import com.Cinema.App.model.Seat;
import com.Cinema.App.model.Showtime;
import com.Cinema.App.model.request.ShowtimeRequest;
import com.Cinema.App.model.response.SeatAvailabilityResponse;
import com.Cinema.App.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Showtime not found with id: " + id));
    }

    public List<Showtime> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    public List<Showtime> getShowtimesByHall(Long hallId) {
        return showtimeRepository.findByHallId(hallId);
    }

    public List<SeatAvailabilityResponse> getSeatsForShowtime(Long showtimeId) {
        Showtime showtime = getShowtimeById(showtimeId);
        Long hallId = showtime.getHall().getId();
        List<Seat> seats = seatRepository.findByHallId(hallId);
        Set<Long> reserved = bookingRepository.findReservedSeatIdsByShowtimeId(showtimeId);
        return seats.stream()
                .map(s -> SeatAvailabilityResponse.from(s, reserved.contains(s.getId())))
                .toList();
    }

    public Showtime createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new InformationNotFoundException("Movie not found with id: " + request.getMovieId()));
        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new InformationNotFoundException("Hall not found with id: " + request.getHallId()));

        validateNoOverlap(request.getHallId(), request, -1L);

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .hall(hall)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .build();
        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, ShowtimeRequest request) {
        Showtime showtime = getShowtimeById(id);
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new InformationNotFoundException("Movie not found with id: " + request.getMovieId()));
        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new InformationNotFoundException("Hall not found with id: " + request.getHallId()));

        validateNoOverlap(request.getHallId(), request, id);

        showtime.setMovie(movie);
        showtime.setHall(hall);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());
        showtime.setPrice(request.getPrice());
        return showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.delete(getShowtimeById(id));
    }

    private void validateNoOverlap(Long hallId, ShowtimeRequest request, Long excludeId) {
        List<Showtime> overlapping = showtimeRepository.findOverlapping(
                hallId, request.getStartTime(), request.getEndTime(), excludeId);
        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Hall is already booked during this time slot");
        }
    }
}
