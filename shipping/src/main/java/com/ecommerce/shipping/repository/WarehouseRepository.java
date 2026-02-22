package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByActiveTrue();
}
