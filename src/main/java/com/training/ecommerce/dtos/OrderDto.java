package com.training.ecommerce.dtos;

import com.training.ecommerce.entities.OrderItem;
import com.training.ecommerce.entities.Payment;
import com.training.ecommerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {

    private int id;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private UserDto userDto;
    private List<OrderItem> orderItemList;
    private Payment payment;
}
