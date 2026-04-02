package com.Cinema.App.model.response;

import com.Cinema.App.model.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private String movieTitle;
    private LocalDateTime showtime;
    private String hallName;
    private String seatLabel;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime bookedAt;
    private LocalDateTime cancelledAt;

    public static BookingResponse from(Booking b) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setMovieTitle(b.getShowtime().getMovie().getTitle());
        r.setShowtime(b.getShowtime().getStartTime());
        r.setHallName(b.getShowtime().getHall().getName());
        r.setSeatLabel(b.getSeat().getLabel());
        r.setTotalPrice(b.getTotalPrice());
        r.setStatus(b.getStatus());
        r.setBookedAt(b.getBookedAt());
        r.setCancelledAt(b.getCancelledAt());
        return r;
    }
}
