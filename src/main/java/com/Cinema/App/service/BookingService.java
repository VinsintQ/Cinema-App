package com.Cinema.App.service;

import com.Cinema.App.exception.InformationNotFoundException;
import com.Cinema.App.exception.SeatAlreadyBookedException;
import com.Cinema.App.exception.ShowtimeAlreadyStartedException;
import com.Cinema.App.model.*;
import com.Cinema.App.model.request.BookingRequest;
import com.Cinema.App.model.response.BookingResponse;
import com.Cinema.App.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;


    private final Map<String, ReentrantLock> seatLocks = new ConcurrentHashMap<>();


    private ReentrantLock getLockForSeat(Long showtimeId, Long seatId) {
        String key = showtimeId + "-" + seatId;
        return seatLocks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmailAddress(email);
        return bookingRepository.findByUserId(user.getId())
                .stream().map(BookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        return BookingResponse.from(findBookingById(id));
    }


    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmailAddress(email);

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new InformationNotFoundException("Showtime not found"));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ShowtimeAlreadyStartedException(
                    "Cannot book seat. Showtime already started at " + showtime.getStartTime()
            );
        }
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new InformationNotFoundException("Seat not found"));

        if (!seat.getHall().getId().equals(showtime.getHall().getId())) {
            throw new RuntimeException("Seat does not belong to this showtime's hall");
        }

        // Get the ReentrantLock for this specific seat+showtime
        ReentrantLock lock = getLockForSeat(request.getShowtimeId(), request.getSeatId());

        // Acquire the lock — only one thread can pass this point for this seat at a time
        // All other threads trying to book the same seat will BLOCK here until lock is released
        lock.lock();
        try {

            if (bookingRepository.existsByShowtimeIdAndSeatIdAndStatusNot(
                    request.getShowtimeId(), request.getSeatId(), "CANCELLED")) {
                throw new SeatAlreadyBookedException(
                        "Seat with id : " + seat.getId() + " and Label "+seat.getLabel()+ " is already reserved for this showtime"
                );
            }

            Booking booking = Booking.builder()
                    .user(user)
                    .showtime(showtime)
                    .seat(seat)
                    .status("CONFIRMED")
                    .totalPrice(showtime.getPrice())
                    .build();

            return BookingResponse.from(bookingRepository.save(booking));

        } finally {

            lock.unlock();
        }
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = findBookingById(id);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getUser().getEmailAddress().equals(email)) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByShowtime(Long showtimeId) {
        return bookingRepository.findByShowtimeId(showtimeId)
                .stream().map(BookingResponse::from).toList();
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Booking not found with id: " + id));
    }
}
