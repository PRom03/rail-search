package org.example.railsearch;

import jakarta.persistence.*;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Train;

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
    @JoinColumn(name = "station_from_id", nullable = false)
    private Station stationFrom;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_to_id", nullable = false)
    private Station stationTo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "distance", precision = 10, scale = 2)
    private BigDecimal distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Station getStationFrom() {
        return stationFrom;
    }

    public void setStationFrom(Optional<Station> stationFrom) {
        this.stationFrom = stationFrom.orElse(null);
    }

    public Station getStationTo() {
        return stationTo;
    }

    public void setStationTo(Optional<Station> stationTo) {
        this.stationTo = stationTo.orElse(null);
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Optional<Ticket> ticket) {
        this.ticket = ticket.orElse(null);
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Optional<Train> train) {
        this.train = train.orElse(null);
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

}