package com.training.ecommerce.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.training.ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            // VERIFICA DELLA FIRMA: Nessuno può fingere di essere Stripe!
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma non valida");
        }

        // Se il pagamento è andato a buon fine
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                // Recuperiamo l'ID ordine dai metadati che abbiamo salvato prima!
                String orderIdStr = session.getMetadata().get("order_id");
                Integer orderId = Integer.parseInt(orderIdStr);

                // 🌟 MOMENTO CRITICO: Sposta l'ordine a PAID e svuota il carrello!
                orderService.confirmPaymentAndCompleteOrder(orderId);
            }
        }

        return ResponseEntity.ok().build(); // Diciamo a Stripe "Ricevuto!"
    }
}
