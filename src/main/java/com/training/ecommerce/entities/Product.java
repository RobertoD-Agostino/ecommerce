package com.training.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.ecommerce.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    private Double price;

    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    private int stockQuantity;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    @JsonIgnore
    private List<CartItem> cartItemList;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<OrderItem> orderItemList;





}
