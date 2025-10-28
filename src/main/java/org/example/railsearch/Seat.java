package org.example.railsearch;

import jakarta.persistence.*;

@Entity
@Table(name = "seat", schema = "public")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_id_gen")
    @SequenceGenerator(name = "seat_id_gen", sequenceName = "seat_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "is_compartment", nullable = false)
    private Boolean isCompartment = false;

    @Column(name = "class", nullable = false)
    private Integer classField;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_car_id", nullable = false)
    private org.example.railsearch.TrainCar trainCar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getIsCompartment() {
        return isCompartment;
    }

    public void setIsCompartment(Boolean isCompartment) {
        this.isCompartment = isCompartment;
    }

    public Integer getClassField() {
        return classField;
    }

    public void setClassField(Integer classField) {
        this.classField = classField;
    }

    public org.example.railsearch.TrainCar getTrainCar() {
        return trainCar;
    }

    public void setTrainCar(org.example.railsearch.TrainCar trainCar) {
        this.trainCar = trainCar;
    }

}