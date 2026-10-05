package org.example.railsearch.Services;

import org.example.railsearch.Entities.PaymentMethod;
import org.example.railsearch.Repositories.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    public PaymentMethod findById(long id) {
        return paymentMethodRepository.findById(id).orElse(null);

    }
    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }
}
