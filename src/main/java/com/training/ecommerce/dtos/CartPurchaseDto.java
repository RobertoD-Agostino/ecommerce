package com.training.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartPurchaseDto {
    private List<CartItemPurchaseDto> cartItemPurchaseDtoList;
}
