package com.training.ecommerce.services;

import com.training.ecommerce.dtos.OrderDto;
import com.training.ecommerce.dtos.UserDto;
import com.training.ecommerce.entities.*;
import com.training.ecommerce.enums.OrderStatus;
import com.training.ecommerce.enums.PaymentMethod;
import com.training.ecommerce.exceptions.CartException;
import com.training.ecommerce.exceptions.OrderException;
import com.training.ecommerce.repositories.*;
import com.training.ecommerce.utils.ProductUtils;
import com.training.ecommerce.utils.UserUtils;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserUtils userUtils;
    private final ProductUtils productUtils;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final OrderItemRepository orderItemRepo;
    private final PurchasedItemRepository purchasedItemRepo;


    @Transactional
    public OrderDto buyNow(String email, String code, String paymentMethodName, int quantity) {

        try {
            User user = userUtils.findUserByEmail(email);
            Product product = productUtils.findProductByCode(code);

            // Validazione quantità
            if (quantity <= 0 || quantity > product.getStockQuantity()) {
                throw new OrderException("Quantità non valida", HttpStatus.BAD_REQUEST);
            }

            // Recupero metodo pagamento
            PaymentMethod paymentMethod = paymentMethodIsPresent(paymentMethodName);

            // Calcolo prezzo totale
            double totalPrice = product.getPrice() * quantity;

            // Creazione ordine + item (solo in memoria)
            Order order = new Order(LocalDateTime.now(), OrderStatus.PENDING, totalPrice, user, new ArrayList<>());

            OrderItem orderItem = new OrderItem(quantity, totalPrice, product, order);
            order.getOrderItemList().add(orderItem);

            // Creazione pagamento
            Payment payment = new Payment(totalPrice, paymentMethod, LocalDateTime.now(), order);
            paymentRepo.save(payment);
            order.setPayment(payment);
            order.setOrderStatus(OrderStatus.PAID);


            // Aggiornamento stock
            product.setStockQuantity(product.getStockQuantity() - quantity);
            PurchasedItem purchasedItem = new PurchasedItem(quantity,user, product);

            // Persistenza
            purchasedItemRepo.save(purchasedItem);
            productRepo.save(product);
            orderItemRepo.save(orderItem);
            orderRepo.save(order); // salva anche OrderItem e Payment grazie al cascade se configurato

            // DTO
            UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getEmail());
            return new OrderDto(order.getId(), order.getOrderDate(), order.getOrderStatus(), userDto, order.getOrderItemList(), payment);
        }catch (OptimisticLockException e) {
            throw new OrderException("Qualcun altro sta acquistando ora lo stesso prodotto. Riprova.", HttpStatus.CONFLICT);
        }
    }

    public PaymentMethod paymentMethodIsPresent(String paymentMethod){
        return Arrays.stream(PaymentMethod.values())
                .filter(pM -> pM.name().equalsIgnoreCase(paymentMethod))
                .findAny()
                .orElseThrow(()-> new CartException("Inserire un metodo di pagamento valido", HttpStatus.BAD_REQUEST));
    }

}
