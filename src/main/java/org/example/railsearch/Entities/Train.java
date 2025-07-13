package org.example.railsearch.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "train", schema = "public")
public class Train {
    @Id
    @ColumnDefault("nextval('train_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "init_station", nullable = false)
    private Station initStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fin_station", nullable = false)
    private Station finStation;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Station getInitStation() {
        return initStation;
    }

    public void setInitStation(Station initStation) {
        this.initStation = initStation;
    }

    public Station getFinStation() {
        return finStation;
    }

    public void setFinStation(Station finStation) {
        this.finStation = finStation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}