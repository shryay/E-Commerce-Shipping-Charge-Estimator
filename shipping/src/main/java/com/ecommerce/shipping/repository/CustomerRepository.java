package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
