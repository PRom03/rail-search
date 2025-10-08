package org.example.railsearch.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "price_km", schema = "public")
public class Price {
    @Id
    @ColumnDefault("nextval('price_km_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "km_floor", nullable = false)
    private Integer kmFloor;

    @Column(name = "km_ceil", nullable = false)
    private Integer kmCeil;

    @Column(name = "price", nullable = false, precision = 5, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKmFloor() {
        return kmFloor;
    }

    public void setKmFloor(Integer kmFloor) {
        this.kmFloor = kmFloor;
    }

    public Integer getKmCeil() {
        return kmCeil;
    }

    public void setKmCeil(Integer kmCeil) {
        this.kmCeil = kmCeil;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

}