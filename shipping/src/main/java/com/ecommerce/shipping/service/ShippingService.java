package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.FullShippingChargeResponse;
import com.ecommerce.shipping.dto.LocationDto;
import com.ecommerce.shipping.dto.NearestWarehouseResponse;
import com.ecommerce.shipping.entity.Customer;
import com.ecommerce.shipping.entity.Product;
import com.ecommerce.shipping.entity.Seller;
import com.ecommerce.shipping.entity.Warehouse;
import com.ecommerce.shipping.entity.enums.DeliverySpeed;
import com.ecommerce.shipping.entity.enums.TransportMode;
import com.ecommerce.shipping.exception.ResourceNotFoundException;
import com.ecommerce.shipping.repository.CustomerRepository;
import com.ecommerce.shipping.repository.ProductRepository;
import com.ecommerce.shipping.repository.SellerRepository;
import com.ecommerce.shipping.repository.WarehouseRepository;
import com.ecommerce.shipping.service.strategy.TransportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Core service that computes shipping charges using transport-mode strategies,
 * the Haversine distance between warehouse and customer, and delivery-speed surcharges.
 */
@Service
@RequiredArgsConstructor
public class ShippingService {

    static final double DISTANCE_THRESHOLD_AIR = 500;
    static final double DISTANCE_THRESHOLD_TRUCK = 100;
    static final double BASE_CHARGE = 10.0;
    static final double EXPRESS_WEIGHT_SURCHARGE = 1.2;

    private final SellerRepository sellerRepo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final DistanceService distanceService;
    private final WarehouseService warehouseService;
    private final Map<String, TransportStrategy> strategies;

    /**
     * API 2 — Calculates shipping charge from a specific warehouse to a customer
     * for a given product and delivery speed.
     */
    @Cacheable(value = "shippingCharges",
            key = "#warehouseId + '-' + #customerId + '-' + #productId + '-' + #speed")
    public double calculateFromWarehouse(Long warehouseId, Long customerId,
                                         Long productId, DeliverySpeed speed) {

        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        return computeCharge(warehouse, customer, product, speed);
    }

    /**
     * API 3 — End-to-end calculation: finds nearest warehouse for the seller,
     * then computes shipping charge from that warehouse to the customer.
     */
    public FullShippingChargeResponse calculateForSeller(Long sellerId, Long customerId,
                                                         DeliverySpeed speed) {

        Seller seller = sellerRepo.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Product product = productRepo.findFirstBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No product found for seller " + sellerId));

        Warehouse nearest = warehouseService.findNearestToLocation(
                seller.getLatitude(), seller.getLongitude());

        double charge = computeCharge(nearest, customer, product, speed);

        return new FullShippingChargeResponse(
                charge,
                new NearestWarehouseResponse(
                        nearest.getId(),
                        new LocationDto(nearest.getLatitude(), nearest.getLongitude())
                )
        );
    }

    private double computeCharge(Warehouse warehouse, Customer customer,
                                 Product product, DeliverySpeed speed) {

        double distance = distanceService.calculate(
                warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude());

        TransportStrategy strategy = resolveStrategy(distance);
        double baseCharge = strategy.calculate(distance, product.getWeightKg());

        return applySpeedSurcharge(baseCharge, product.getWeightKg(), speed);
    }

    TransportStrategy resolveStrategy(double distance) {
        if (distance > DISTANCE_THRESHOLD_AIR) return strategies.get(TransportMode.AIR.name());
        if (distance > DISTANCE_THRESHOLD_TRUCK) return strategies.get(TransportMode.TRUCK.name());
        return strategies.get(TransportMode.MINIVAN.name());
    }

    private double applySpeedSurcharge(double baseCharge, double weightKg,
                                       DeliverySpeed speed) {
        if (speed == DeliverySpeed.EXPRESS) {
            return BASE_CHARGE + (EXPRESS_WEIGHT_SURCHARGE * weightKg) + baseCharge;
        }
        return BASE_CHARGE + baseCharge;
    }
}
