package org.example.railsearch.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "train_car", schema = "public")
public class TrainCar {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "train_car_id_gen")
    @SequenceGenerator(name = "train_car_id_gen", sequenceName = "train_car_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cartype_id")
    private Cartype cartype;

    @Column(name = "number")
    private Integer number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id")
    private Train train;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cartype getCartype() {
        return cartype;
    }

    public void setCartype(Cartype cartype) {
        this.cartype = cartype;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

}