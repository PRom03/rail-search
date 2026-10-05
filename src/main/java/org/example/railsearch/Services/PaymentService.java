package org.example.railsearch.Services;

import org.example.railsearch.Entities.Payment;
import org.example.railsearch.Repositories.PaymentMethodRepository;
import org.example.railsearch.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public Payment findById(long id) {
        return paymentRepository.findById(id);
    }
    public record PaymentCreateDto(String extId,String methodName){}
    public Payment create(PaymentCreateDto payment) {
        Payment payment1 = new Payment();
        payment1.setExternalId(payment.extId());
        payment1.setMethod(paymentMethodRepository.findByName(payment.methodName()));
        return paymentRepository.save(payment1);
    }
}
