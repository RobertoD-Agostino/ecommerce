package com.training.ecommerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {

    private Integer id;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private UserDto userDto;
    private OrderItemDto orderItemDto;
    private List<OrderItemDto> orderItemListDto;
    private Payment payment;

    public OrderDto(Integer id, LocalDateTime orderDate, OrderStatus orderStatus, UserDto userDto, OrderItemDto orderItemDto, Payment payment) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.userDto = userDto;
        this.orderItemDto = orderItemDto;
        this.payment = payment;
    }

    public OrderDto(Integer id, LocalDateTime orderDate, OrderStatus orderStatus, UserDto userDto, List<OrderItemDto> orderItemListDto, Payment payment) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.userDto = userDto;
        this.orderItemListDto = orderItemListDto;
        this.payment = payment;
    }
}
