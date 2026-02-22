package com.ecommerce.shipping.service;

import com.ecommerce.shipping.entity.Product;
import com.ecommerce.shipping.entity.Seller;
import com.ecommerce.shipping.entity.Warehouse;
import com.ecommerce.shipping.exception.ResourceNotFoundException;
import com.ecommerce.shipping.repository.ProductRepository;
import com.ecommerce.shipping.repository.SellerRepository;
import com.ecommerce.shipping.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

/**
 * Handles warehouse lookup operations, including finding the nearest active
 * warehouse to a seller's location.
 */
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final SellerRepository sellerRepo;
    private final ProductRepository productRepo;
    private final DistanceService distanceService;

    /**
     * Finds the nearest active warehouse for a seller after validating
     * that the product belongs to the given seller.
     */
    public Warehouse findNearest(Long sellerId, Long productId) {
        Seller seller = sellerRepo.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException(
                    "Product " + productId + " does not belong to seller " + sellerId);
        }

        return findNearestToLocation(seller.getLatitude(), seller.getLongitude());
    }

    /**
     * Finds the nearest active warehouse to a given geographic coordinate.
     *
     * @throws ResourceNotFoundException if no active warehouse exists
     */
    public Warehouse findNearestToLocation(double latitude, double longitude) {
        return warehouseRepo.findByActiveTrue().stream()
                .min(Comparator.comparingDouble(w ->
                        distanceService.calculate(
                                latitude, longitude,
                                w.getLatitude(), w.getLongitude())))
                .orElseThrow(() -> new ResourceNotFoundException("No active warehouse found"));
    }
}
