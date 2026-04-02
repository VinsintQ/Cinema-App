package com.Cinema.App.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    private int rowNumber;
    private int seatNumber;
    private String label;

    @Version
    private Long version;
}
