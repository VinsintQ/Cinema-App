package com.Cinema.App.repository;

import com.Cinema.App.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieId(Long movieId);
    List<Showtime> findByHallId(Long hallId);


    @Query("SELECT s FROM Showtime s WHERE s.hall.id = :hallId " +
           "AND s.id != :excludeId " +
           "AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Showtime> findOverlapping(Long hallId, LocalDateTime startTime, LocalDateTime endTime, Long excludeId);
}
