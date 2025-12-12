package com.training.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(mappedBy = "cart")
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart")
    @ToString.Exclude
    private List<CartItem> cartItemList;

    public Cart(User user, List<CartItem> cartItemList) {
        this.user = user;
        this.cartItemList = cartItemList;
    }
}
