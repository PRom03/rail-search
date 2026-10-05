package org.example.railsearch.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "transporter", schema = "public")
public class Transporter {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transporter_id_gen")
    @SequenceGenerator(name = "transporter_id_gen", sequenceName = "transporter_id_seq", allocationSize = 1)
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