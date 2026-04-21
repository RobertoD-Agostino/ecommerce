package com.training.ecommerce.controllers;

import com.stripe.exception.StripeException;
import com.training.ecommerce.entities.Order;
import com.training.ecommerce.services.OrderService;
import com.training.ecommerce.services.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;
    private final StripeService stripeService;

    @PostMapping("/create-session")
    public ResponseEntity<String> checkout(@AuthenticationPrincipal Jwt jwt) throws StripeException {
        String email = jwt.getSubject(); // Email sicura estratta dal token!

        // 1. Il tuo OrderService prende il carrello dell'utente e crea un Ordine PENDING nel DB
        Order order = orderService.createOrderFromCart(email);

        // 2. StripeService genera l'URL di pagamento
        String stripeUrl = stripeService.createCheckoutSession(order);

        // 3. Restituisci l'URL al frontend per il redirect
        return ResponseEntity.ok(stripeUrl);
    }
}
