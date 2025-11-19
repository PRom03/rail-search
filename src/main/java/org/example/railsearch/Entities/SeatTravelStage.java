package org.example.railsearch.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seat_travel_stage", schema = "public")
public class SeatTravelStage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_travel_stage_id_gen")
    @SequenceGenerator(name = "seat_travel_stage_id_gen", sequenceName = "seat_travel_stage_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_train_date_id")
    private SeatTrainDate seatTrainDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_stage_id")
    private TravelStage travelStage;

    public SeatTrainDate getSeatTrainDate() {
        return seatTrainDate;
    }

    public void setSeatTrainDate(SeatTrainDate seatTrainDate) {
        this.seatTrainDate = seatTrainDate;
    }

    public TravelStage getTravelStage() {
        return travelStage;
    }

    public void setTravelStage(TravelStage travelStage) {
        this.travelStage = travelStage;
    }
}