package org.example.railsearch;

import jakarta.persistence.*;

@Entity
@Table(name = "cartype", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "cartype_name_key", columnNames = {"name"})
})
public class Cartype {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cartype_id_gen")
    @SequenceGenerator(name = "cartype_id_gen", sequenceName = "cartype_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 16)
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