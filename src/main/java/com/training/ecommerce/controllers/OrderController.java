package com.training.ecommerce.controllers;

import com.training.ecommerce.dtos.OrderDto;
import com.training.ecommerce.entities.Order;
import com.training.ecommerce.enums.PaymentMethod;
import com.training.ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/buyNow")
    public ResponseEntity<OrderDto> buyNow(@RequestParam String email, @RequestParam String code, @RequestParam String paymentMethod, @RequestParam int quantity){
        OrderDto ret = orderService.buyNow(email, code, paymentMethod, quantity);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
