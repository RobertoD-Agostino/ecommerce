package com.training.ecommerce.orderTests;

import com.training.ecommerce.dtos.CartItemPurchaseDto;
import com.training.ecommerce.dtos.CartPurchaseDto;
import com.training.ecommerce.dtos.OrderDto;
import com.training.ecommerce.entities.Cart;
import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.entities.Product;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.enums.ProductCategory;
import com.training.ecommerce.repositories.*;
import com.training.ecommerce.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Slf4j
@SpringBootTest
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class BuyFromCartTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeAll
    public void init() {

        // 1 — Creazione user
        User u = new User("Mario", "Rossi", "mariorossi@gmail.com");
        userRepository.save(u);

        // 3 — Prodotti
        Product p1 = new Product("Lenovo Thinkpad 16", 2000.00 ,"EL001", ProductCategory.ELECTRONICS, 10);
        Product p2 = new Product("Samsung Galaxy S25", 850.00 ,"EL002", ProductCategory.ELECTRONICS, 10);
        productRepository.save(p1);
        productRepository.save(p2);

        // 2 — Creazione carrello

        Cart cart = new Cart(u, new ArrayList<>());
        cart.getCartItemList().add(new CartItem(2, cart, p1));
        cart.getCartItemList().add(new CartItem(1, cart, p2));
        cartRepository.save(cart);
        u.setCart(cart);

    }


    @Test
    @Order(1)
    public void testBuyFromCart() {

        CartPurchaseDto dto = new CartPurchaseDto();
        dto.setCartItemPurchaseDtoList(
                List.of(
                        new CartItemPurchaseDto("EL001", 2),
                        new CartItemPurchaseDto("EL002", 1)
                )
        );

        OrderDto result = orderService.buyFromCart(
                "mariorossi@gmail.com",
                "VISA",
                dto
        );

        assertNotNull(result);
        assertEquals(2, result.getOrderItemListDto().size());
        assertEquals(140.0, result.getPayment().getAmount());  // 100 + 40


        //---------------------------------------
        // ✔ Verifica con JDBC Template (DB reale)
        //---------------------------------------
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                Integer.class,
                "mariorossi@gmail.com"
        );

        assertEquals(1, count);

        //---------------------------------------
        // Verifico anche che la quantità stock sia aggiornata
        //---------------------------------------
        Integer stock1 = jdbcTemplate.queryForObject(
                "SELECT stock_quantity FROM product WHERE code = 'EL001'",
                Integer.class
        );
        Integer stock2 = jdbcTemplate.queryForObject(
                "SELECT stock_quantity FROM product WHERE code = 'EL002'",
                Integer.class
        );

        assertEquals(8, stock1); // 10 - 2
        assertEquals(4, stock2); // 5 - 1
    }
}
