package com.booking.football_api.controller;

import com.booking.football_api.entity.Payment;
import com.booking.football_api.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("Thành công");
        return paymentRepository.save(payment);
    }
}