package com.Cinema.App.model.response;

import com.Cinema.App.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private Long id;
    private int rowNumber;
    private int seatNumber;
    private String label;
    private boolean reserved;

    public static SeatAvailabilityResponse from(Seat seat, boolean reserved) {
        return new SeatAvailabilityResponse(
                seat.getId(),
                seat.getRowNumber(),
                seat.getSeatNumber(),
                seat.getLabel(),
                reserved
        );
    }
}
