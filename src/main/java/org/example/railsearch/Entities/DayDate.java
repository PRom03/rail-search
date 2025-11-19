package org.example.railsearch.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "date", schema = "public")
public class DayDate {
    @Id
    @SequenceGenerator(name = "date_id_gen", sequenceName = "payment_id_seq", allocationSize = 1)
    @Column(name = "date", nullable = false)
    private LocalDate id;

    public LocalDate getId() {
        return id;
    }

    public void setId(LocalDate id) {
        this.id = id;
    }

}