package com.Cinema.App.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "halls")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int rows;

    private int seatsPerRow;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
