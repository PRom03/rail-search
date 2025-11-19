package org.example.railsearch.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seat_train_date", schema = "public")
public class SeatTrainDate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_train_date_id_gen")
    @SequenceGenerator(name = "seat_train_date_id_gen", sequenceName = "seat_train_date_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_date_id")
    private TrainDate trainDate;

}