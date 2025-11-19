package org.example.railsearch.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_method", schema = "public")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_method_id_gen")
    @SequenceGenerator(name = "payment_method_id_gen", sequenceName = "payment_method_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 32)
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