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



    @Column(name = "name", nullable = false, length = 100)
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

}