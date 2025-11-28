package com.training.ecommerce.repositories;

import com.training.ecommerce.entities.PurchasedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedItemRepository extends JpaRepository<PurchasedItem, Integer>{

}
