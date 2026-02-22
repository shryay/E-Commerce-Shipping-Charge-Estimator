package com.ecommerce.shipping.service;

import com.ecommerce.shipping.entity.Product;
import com.ecommerce.shipping.entity.Seller;
import com.ecommerce.shipping.entity.Warehouse;
import com.ecommerce.shipping.exception.ResourceNotFoundException;
import com.ecommerce.shipping.repository.ProductRepository;
import com.ecommerce.shipping.repository.SellerRepository;
import com.ecommerce.shipping.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock private WarehouseRepository warehouseRepo;
    @Mock private SellerRepository sellerRepo;
    @Mock private ProductRepository productRepo;
    @Mock private DistanceService distanceService;

    @InjectMocks
    private WarehouseService warehouseService;

    private Seller seller;
    private Product product;
    private Warehouse blrWarehouse;
    private Warehouse mumWarehouse;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setLatitude(12.97);
        seller.setLongitude(77.59);

        product = new Product();
        product.setId(1L);
        product.setSeller(seller);

        blrWarehouse = new Warehouse();
        blrWarehouse.setId(1L);
        blrWarehouse.setLatitude(12.99);
        blrWarehouse.setLongitude(77.60);
        blrWarehouse.setActive(true);

        mumWarehouse = new Warehouse();
        mumWarehouse.setId(2L);
        mumWarehouse.setLatitude(19.07);
        mumWarehouse.setLongitude(72.87);
        mumWarehouse.setActive(true);
    }

    @Test
    void shouldReturnNearestWarehouse() {
        when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findByActiveTrue())
                .thenReturn(List.of(blrWarehouse, mumWarehouse));
        when(distanceService.calculate(12.97, 77.59, 12.99, 77.60)).thenReturn(2.5);
        when(distanceService.calculate(12.97, 77.59, 19.07, 72.87)).thenReturn(800.0);

        Warehouse nearest = warehouseService.findNearest(1L, 1L);

        assertEquals(blrWarehouse.getId(), nearest.getId());
    }

    @Test
    void shouldThrowWhenSellerNotFound() {
        when(sellerRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearest(99L, 1L));
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearest(1L, 99L));
    }

    @Test
    void shouldThrowWhenProductDoesNotBelongToSeller() {
        Seller otherSeller = new Seller();
        otherSeller.setId(2L);
        product.setSeller(otherSeller);

        when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class,
                () -> warehouseService.findNearest(1L, 1L));
    }

    @Test
    void shouldThrowWhenNoActiveWarehouses() {
        when(warehouseRepo.findByActiveTrue()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearestToLocation(12.97, 77.59));
    }
}
