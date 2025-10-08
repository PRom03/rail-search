package org.example.railsearch.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "distance", schema = "public", indexes = {
        @Index(name = "distance_idx", columnList = "station1_id, station2_id", unique = true)
})
public class Distance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "distance_id_seq")
    @SequenceGenerator(name = "distance_id_seq",
            sequenceName = "distance_id_seq",
            allocationSize = 1)
    private Integer id;



    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station1_id", nullable = false)
    private Station station1;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station2_id", nullable = false)
    private Station station2;

    @Column(name = "distance", nullable = false, precision = 10, scale = 3)
    private BigDecimal distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Station getStation1() {
        return station1;
    }

    public void setStation1(Station station1) {
        this.station1 = station1;
    }

    public Station getStation2() {
        return station2;
    }

    public void setStation2(Station station2) {
        this.station2 = station2;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }
    @Override
    public String toString() {
        return getStation1().getName()+"\t"+getStation2().getName()+"\t"+getDistance();
    }
}