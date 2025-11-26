package com.training.ecommerce.services;

import com.training.ecommerce.dtos.OrderDto;
import com.training.ecommerce.dtos.UserDto;
import com.training.ecommerce.entities.*;
import com.training.ecommerce.enums.OrderStatus;
import com.training.ecommerce.enums.PaymentMethod;
import com.training.ecommerce.exceptions.CartException;
import com.training.ecommerce.exceptions.OrderException;
import com.training.ecommerce.repositories.OrderItemRepository;
import com.training.ecommerce.repositories.OrderRepository;
import com.training.ecommerce.repositories.PaymentRepository;
import com.training.ecommerce.repositories.ProductRepository;
import com.training.ecommerce.utils.ProductUtils;
import com.training.ecommerce.utils.UserUtils;
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

//    public Order createOrderFromCart(){
//
//    }

    @Transactional
    public OrderDto buyNow (String email, String code, String paymentMethodName, int quantity){
        Product product = productUtils.findProductByCode(code);
        User user = userUtils.findUserByEmail(email);
        Order order = createOrder(user, product, quantity);
        PaymentMethod paymentMethod = paymentMethodIsPresent(paymentMethodName);
        Payment payment = new Payment(order.getTotalAmount(), paymentMethod, LocalDateTime.now(), order);
        paymentRepo.save(payment);

        order.setPayment(payment);
        order.setOrderStatus(OrderStatus.PAID);
        int updatedQuantity = product.getStockQuantity()-quantity;
        product.setStockQuantity(updatedQuantity);

        productRepo.save(product);
        orderRepo.save(order);
        UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getEmail());
        return new OrderDto(order.getId(), order.getOrderDate(),order.getOrderStatus(), userDto, order.getOrderItemList(),payment);
    }

    public Order createOrder(User user, Product product, int quantity){
        if(quantity>0 && quantity<=product.getStockQuantity()){
            double totalOrderPrice = product.getPrice() * quantity;
            List<OrderItem> orderItemList = new ArrayList<>();

            Order order = new Order(LocalDateTime.now(), OrderStatus.PENDING ,totalOrderPrice, user, orderItemList);
            OrderItem orderItem = new OrderItem(quantity, totalOrderPrice, product, order);
            orderItemRepo.save(orderItem);

            order.getOrderItemList().add(orderItem);
            return orderRepo.save(order);
        }else{
            throw new OrderException("Inserire una quantità diversa", HttpStatus.BAD_REQUEST);
        }
    }

//    public Payment createPayment(Order order, String paymentMethod){
//        if(paymentMethodIsPresent(paymentMethod)){
//            return paymentRepo.save(new Payment(order.getTotalAmount(), paymentMethod, LocalDateTime.now(), order));
//        }else{
//            throw new OrderException("Inserire un metodo di pagamento valido", HttpStatus.BAD_REQUEST);
//        }
//    }

    public PaymentMethod paymentMethodIsPresent(String paymentMethod){
        return Arrays.stream(PaymentMethod.values())
                .filter(pM -> pM.name().equalsIgnoreCase(paymentMethod))
                .findAny()
                .orElseThrow(()-> new CartException("Inserire un metodo di pagamento valido", HttpStatus.BAD_REQUEST));
    }

}
