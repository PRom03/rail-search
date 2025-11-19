package org.example.railsearch.Entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "train_date", schema = "public")
public class TrainDate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "train_date_id_gen")
    @SequenceGenerator(name = "train_date_id_gen", sequenceName = "train_date_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id")
    private Train train;

    @Column(name = "date")
    private LocalDate date;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    
}