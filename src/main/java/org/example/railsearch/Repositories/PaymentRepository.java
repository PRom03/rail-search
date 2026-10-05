package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findById(long id);
}
