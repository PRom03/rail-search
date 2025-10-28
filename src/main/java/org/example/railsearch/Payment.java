package org.example.railsearch;

import jakarta.persistence.*;

@Entity
@Table(name = "payment", schema = "public")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_gen")
    @SequenceGenerator(name = "payment_id_gen", sequenceName = "payment_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "method_id", nullable = false)
    private org.example.railsearch.PaymentMethod method;

    @Column(name = "external_id", length = 10)
    private String externalId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public org.example.railsearch.PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(org.example.railsearch.PaymentMethod method) {
        this.method = method;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}