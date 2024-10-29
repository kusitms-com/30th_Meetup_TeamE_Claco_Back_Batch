package com.curateme.clacobatchserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "score")
    private Double score;

    @ManyToOne
    @JoinColumn(name = "concertId", nullable = false)
    private Concert concert;

    public ConcertCategory(String category, Double score, Concert concert) {
        this.category = category;
        this.score = score;
        this.concert = concert;
    }
}
