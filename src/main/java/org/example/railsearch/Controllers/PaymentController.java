package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Payment;
import org.example.railsearch.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @GetMapping("/{id}")
    public Payment findById(@PathVariable long id) {
        return paymentService.findById(id);
    }
    @PostMapping("/")
    public Payment save(@RequestBody PaymentService.PaymentCreateDto payment) {
        return paymentService.create(payment);
    }
}
