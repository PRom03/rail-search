package org.example.railsearch.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "station", schema = "public")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "station_seq")
    @SequenceGenerator(
            name = "station_seq",
            sequenceName = "station_id_seq",
            allocationSize = 1
    )
    @ColumnDefault("nextval('station_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 34)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object st2) {
        Station s2 = (Station) st2;
        if (this.id == ((Station) st2).id) return true;
        else return false;
    }

}