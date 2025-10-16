package org.example.railsearch.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Embeddable
public class DistanceId implements java.io.Serializable {
    private static final long serialVersionUID = -7196641673325587563L;
    @ColumnDefault("nextval('distance_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "station1_id", nullable = false)
    private Integer station1Id;

    @Column(name = "station2_id", nullable = false)
    private Integer station2Id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStation1Id() {
        return station1Id;
    }

    public void setStation1Id(Integer station1Id) {
        this.station1Id = station1Id;
    }

    public Integer getStation2Id() {
        return station2Id;
    }

    public void setStation2Id(Integer station2Id) {
        this.station2Id = station2Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DistanceId entity = (DistanceId) o;
        return Objects.equals(this.station1Id, entity.station1Id) &&
                Objects.equals(this.station2Id, entity.station2Id) &&
                Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station1Id, station2Id, id);
    }

}