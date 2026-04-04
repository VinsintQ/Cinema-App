package com.Cinema.App.repository;

import com.Cinema.App.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);
    List<Booking> findByShowtimeId(Long showtimeId);

    @Query("SELECT b.seat.id FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status != 'CANCELLED'")
    Set<Long> findReservedSeatIdsByShowtimeId(Long showtimeId);

    boolean existsByShowtimeIdAndSeatIdAndStatusNot(Long showtimeId, Long seatId, String status);
}
