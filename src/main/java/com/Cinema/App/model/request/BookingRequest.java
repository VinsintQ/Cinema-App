package com.Cinema.App.model.request;

import lombok.Data;

@Data
public class BookingRequest {
    private Long showtimeId;
    private Long seatId;
}
