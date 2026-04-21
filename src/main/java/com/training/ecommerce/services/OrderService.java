package com.training.ecommerce.services;

import com.training.ecommerce.dtos.*;
import com.training.ecommerce.entities.*;
import com.training.ecommerce.enums.OrderStatus;
import com.training.ecommerce.enums.PaymentMethod;
import com.training.ecommerce.exceptions.CartException;
import com.training.ecommerce.exceptions.OrderException;
import com.training.ecommerce.repositories.*;
import com.training.ecommerce.utils.ProductUtils;
import com.training.ecommerce.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;


    public Order createOrderFromCart(String email) {
        User user = userUtils.findUserByEmail(email);
        Cart cart = user.getCart();

        if (cart.getCartItemList().isEmpty()) {
            throw new CartException("Il carrello è vuoto", HttpStatus.BAD_REQUEST);
        }

        // 1. Creiamo l'oggetto Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        // 2. Trasformiamo i CartItem in OrderItem (per salvare il prezzo storico)
        List<OrderItem> orderItems = cart.getCartItemList().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice()); // Prezzo attuale
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setOrderItemList(orderItems);

        // 3. Calcoliamo il totale dell'ordine
        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(total);

        // 4. Salviamo l'ordine (Cascade farà il resto per gli OrderItem)
        return orderRepo.save(order);
    }

    @Transactional
    public void confirmPaymentAndCompleteOrder(Integer orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            return; // Evitiamo di processare due volte lo stesso ordine
        }

        // 1. Aggiorniamo lo stato dell'ordine
        order.setOrderStatus(OrderStatus.PAID); // o CONFIRMED/SHIPPED secondo il tuo Enum

        // 2. Scarichiamo il magazzino (Stock)
        for (OrderItem item : order.getOrderItemList()) {
            Product product = item.getProduct();
            int newStock = product.getStockQuantity() - item.getQuantity();
            if (newStock < 0) {
                throw new RuntimeException("Stock insufficiente per il prodotto: " + product.getName());
            }
            product.setStockQuantity(newStock);
            productRepo.save(product);
        }

        // 3. Svuotiamo il carrello dell'utente
        cartService.clearCart(order.getUser().getEmail());

        orderRepo.save(order);
    }


    @Transactional
    public OrderDto buyNow(String email, String code, String paymentMethodName, int quantity) {

        try {
            User user = userUtils.findUserByEmail(email);
            Product product = productUtils.findProductByCode(code);

            if (quantity <= 0 || quantity > product.getStockQuantity()) {
                throw new OrderException("Quantità non valida", HttpStatus.BAD_REQUEST);
            }

            double totalPrice = product.getPrice() * quantity;

            Order order = new Order(LocalDateTime.now(), OrderStatus.PENDING, totalPrice, user, new ArrayList<>());
            OrderItem orderItem = new OrderItem(quantity, totalPrice, product, order);
            OrderItemDto orderItemDto = new OrderItemDto(quantity, product);
            order.getOrderItemList().add(orderItem);

            PaymentMethod paymentMethod = paymentMethodIsPresent(paymentMethodName);
            Payment payment = new Payment(totalPrice, paymentMethod, LocalDateTime.now(), order);

            paymentRepo.save(payment);
            order.setPayment(payment);
            order.setOrderStatus(OrderStatus.PAID);
            product.setStockQuantity(product.getStockQuantity() - quantity);

            PurchasedItem purchasedItem = new PurchasedItem(quantity,user, product);

            purchasedItemRepo.save(purchasedItem);
            productRepo.save(product);
            orderItemRepo.save(orderItem);
            orderRepo.save(order);

            UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getEmail());
            return new OrderDto(order.getId(), order.getOrderDate(), order.getOrderStatus(), userDto, orderItemDto, payment);
        }catch (OptimisticLockException e) {
            throw new OrderException("Qualcun altro sta acquistando ora lo stesso prodotto. Riprova.", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public OrderDto buyFromCart(String email, String paymentMethodName, CartPurchaseDto cartPurchaseDto){
        try {
            User user = userUtils.findUserByEmail(email);
            List<CartItemPurchaseDto> itemsToBuy = cartPurchaseDto.getCartItemPurchaseDtoList();

            double totalPrice = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            List<CartItem> cartItemToDelete = new ArrayList<>();

            // Ciclo UNICO: Calcola Totale e Prepara gli OrderItem
            for(CartItemPurchaseDto item : itemsToBuy){
                // Trova il CartItem per l'utente, assicurando che esista nel carrello
                CartItem cartItem = cartItemRepository.findByProduct_CodeAndCart_User_Email(item.getCode(), email)
                        .orElseThrow(()-> new CartException("L'articolo con codice " + item.getCode() + " non è stato trovato nel carrello.", HttpStatus.NOT_FOUND));

                Product product = cartItem.getProduct();
                int quantity = item.getQuantity();
                if(quantity>product.getStockQuantity() || quantity>cartItem.getQuantity()){
                    throw new OrderException("La quantità selezionata è troppo grande", HttpStatus.BAD_REQUEST);
                }

                double itemPrice = quantity * product.getPrice();
                totalPrice += itemPrice;

                // Crea OrderItem con il prezzo CORRETTO della riga
                OrderItem orderItem = new OrderItem(quantity, itemPrice, product, null);
                orderItems.add(orderItem);
                cartItemToDelete.add(cartItem);


                orderItemDtos.add(new OrderItemDto(quantity, product));
            }

            // Crea Order e collega l'Order a tutti gli OrderItem
            Order order = new Order(LocalDateTime.now(), OrderStatus.PENDING, totalPrice, user, orderItems);
            orderItems.forEach(item -> item.setOrder(order));

            PaymentMethod paymentMethod = paymentMethodIsPresent(paymentMethodName);
            Payment payment = new Payment(totalPrice, paymentMethod, LocalDateTime.now(), order);
            paymentRepo.save(payment);

            order.setPayment(payment);
            order.setOrderStatus(OrderStatus.PAID);

            // Ciclo per Aggiornamento DB e Pulizia
            for(OrderItem orderItem : orderItems){
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
                PurchasedItem purchasedItem = new PurchasedItem(orderItem.getQuantity(), user, product);

                productRepo.save(product);
                orderItemRepo.save(orderItem);
                purchasedItemRepo.save(purchasedItem);
            }

            cartItemRepository.deleteAll(cartItemToDelete);
            orderRepo.save(order);

            UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getEmail());
            return new OrderDto(order.getId(), order.getOrderDate(), order.getOrderStatus(), userDto, orderItemDtos, payment);
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
