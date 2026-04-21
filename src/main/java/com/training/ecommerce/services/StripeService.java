package com.training.ecommerce.services;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.training.ecommerce.entities.Order;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public String createCheckoutSession(Order order) throws StripeException {
        // Stripe ragiona in CENTESIMI (Es: 10.00€ diventano 1000)
        long amountInCents = Math.round(order.getTotalAmount() * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Dove mandare l'utente se il pagamento riesce o fallisce
                .setSuccessUrl("http://localhost:3000/success?orderId=" + order.getId())
                .setCancelUrl("http://localhost:3000/cancel")
                .setCustomerEmail(order.getUser().getEmail()) // Usiamo l'email sicura!
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Ordine #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                // Fondamentale per ritrovare l'ordine nel Webhook!
                .putMetadata("order_id", String.valueOf(order.getId()))
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // Questo è il link magico da dare al Frontend!
    }
}