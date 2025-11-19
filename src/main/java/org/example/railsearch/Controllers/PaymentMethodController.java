package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Payment;
import org.example.railsearch.Entities.PaymentMethod;
import org.example.railsearch.Services.PaymentMethodService;
import org.example.railsearch.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
    @RequestMapping("/payment-methods")
    public class PaymentMethodController {
        @Autowired
        private PaymentMethodService paymentMethodService;
        @GetMapping("/{id}")
        public PaymentMethod findById(@PathVariable long id) {
            return paymentMethodService.findById(id);
        }
        @GetMapping("/")
        public List<PaymentMethod> findAll() {
            return paymentMethodService.findAll();
    }
}
