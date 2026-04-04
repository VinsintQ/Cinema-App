package com.Cinema.App.service;

import com.Cinema.App.exception.InformationNotFoundException;
import com.Cinema.App.model.*;
import com.Cinema.App.model.request.BookingRequest;
import com.Cinema.App.model.response.BookingResponse;
import com.Cinema.App.repository.*;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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


        Seat seat = seatRepository.findByIdWithLock(request.getSeatId())
                .orElseThrow(() -> new InformationNotFoundException("Seat not found"));


        if (!seat.getHall().getId().equals(showtime.getHall().getId())) {
            throw new RuntimeException("Seat does not belong to this showtime's hall");
        }


        if (bookingRepository.existsByShowtimeIdAndSeatIdAndStatusNot(
                request.getShowtimeId(), request.getSeatId(), "CANCELLED")) {
            throw new RuntimeException("Seat " + seat.getLabel() + " is already reserved for this showtime");
        }

        try {
            Booking booking = Booking.builder()
                    .user(user)
                    .showtime(showtime)
                    .seat(seat)
                    .status("CONFIRMED")
                    .totalPrice(showtime.getPrice())
                    .build();
            return BookingResponse.from(bookingRepository.save(booking));
        } catch (OptimisticLockException | OptimisticLockingFailureException e) {
            throw new RuntimeException("Seat was just booked by someone else. Please try another seat.");
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
