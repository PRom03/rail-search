package org.example.railsearch.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "travel_stage", schema = "public")
public class TravelStage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "travel_stage_id_gen")
    @SequenceGenerator(name = "travel_stage_id_gen", sequenceName = "travel_stage_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "stop_from_id", nullable = false)
    private Stop stopFrom;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "stop_to_id", nullable = false)
    private Stop stopTo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "train_date_id", nullable = false)
    private TrainDate trainDate;

    @Column(name = "distance", precision = 10, scale = 2)
    private BigDecimal distance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_train_date_id")
    private SeatTrainDate seatTrainDate;

    public SeatTrainDate getSeatTrainDate() {
        return seatTrainDate;
    }

    public void setSeatTrainDate(SeatTrainDate seatTrainDate) {
        this.seatTrainDate = seatTrainDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Stop getStopFrom() {
        return stopFrom;
    }

    public void setStopFrom(Optional<Stop> stopFrom) {
        this.stopFrom = stopFrom.orElse(null);
    }

    public Stop getStopTo() {
        return stopTo;
    }

    public void setStopTo(Optional<Stop> stopTo) {
        this.stopTo = stopTo.orElse(null);
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Optional<Ticket> ticket) {
        this.ticket = ticket.orElse(null);
    }



    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public TrainDate getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Optional<TrainDate> trainDate) {
        this.trainDate = trainDate.orElse(null);
    }
}