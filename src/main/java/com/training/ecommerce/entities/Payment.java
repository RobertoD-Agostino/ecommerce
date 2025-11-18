package com.training.ecommerce.entities;

import com.training.ecommerce.enums.PaymentMethod;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private Double amount;

    private PaymentMethod paymentMethod;

    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
