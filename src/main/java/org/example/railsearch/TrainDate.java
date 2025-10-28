package org.example.railsearch;

import jakarta.persistence.*;
import org.example.railsearch.Entities.Train;

@Entity
@Table(name = "train_date", schema = "public")
public class TrainDate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "train_date_id_gen")
    @SequenceGenerator(name = "train_date_id_gen", sequenceName = "train_date_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

}