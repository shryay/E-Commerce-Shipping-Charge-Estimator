package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findFirstBySellerId(Long sellerId);
}
