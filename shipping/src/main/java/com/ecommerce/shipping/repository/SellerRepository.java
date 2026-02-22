package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
