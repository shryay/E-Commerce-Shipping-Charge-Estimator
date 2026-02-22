package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.FullShippingChargeResponse;
import com.ecommerce.shipping.entity.Customer;
import com.ecommerce.shipping.entity.Product;
import com.ecommerce.shipping.entity.Seller;
import com.ecommerce.shipping.entity.Warehouse;
import com.ecommerce.shipping.entity.enums.DeliverySpeed;
import com.ecommerce.shipping.exception.ResourceNotFoundException;
import com.ecommerce.shipping.repository.CustomerRepository;
import com.ecommerce.shipping.repository.ProductRepository;
import com.ecommerce.shipping.repository.SellerRepository;
import com.ecommerce.shipping.repository.WarehouseRepository;
import com.ecommerce.shipping.service.strategy.AirStrategy;
import com.ecommerce.shipping.service.strategy.MiniVanStrategy;
import com.ecommerce.shipping.service.strategy.TransportStrategy;
import com.ecommerce.shipping.service.strategy.TruckStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock private SellerRepository sellerRepo;
    @Mock private CustomerRepository customerRepo;
    @Mock private ProductRepository productRepo;
    @Mock private WarehouseRepository warehouseRepo;
    @Mock private DistanceService distanceService;
    @Mock private WarehouseService warehouseService;

    private ShippingService shippingService;

    private Seller seller;
    private Customer customer;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        Map<String, TransportStrategy> strategies = Map.of(
                "MINIVAN", new MiniVanStrategy(),
                "TRUCK", new TruckStrategy(),
                "AIR", new AirStrategy()
        );
        shippingService = new ShippingService(
                sellerRepo, customerRepo, productRepo, warehouseRepo,
                distanceService, warehouseService, strategies);

        seller = new Seller();
        seller.setId(1L);
        seller.setLatitude(12.97);
        seller.setLongitude(77.59);

        customer = new Customer();
        customer.setId(1L);
        customer.setLatitude(13.08);
        customer.setLongitude(80.27);

        product = new Product();
        product.setId(1L);
        product.setWeightKg(5.0);
        product.setSeller(seller);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setLatitude(12.99);
        warehouse.setLongitude(77.60);
        warehouse.setActive(true);
    }

    // ── helpers ──────────────────────────────────────────────

    private void stubWarehouseLookups(double distance) {
        when(warehouseRepo.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(distanceService.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(distance);
    }

    private void stubSellerLookups(double distance) {
        when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepo.findFirstBySellerId(1L)).thenReturn(Optional.of(product));
        when(warehouseService.findNearestToLocation(anyDouble(), anyDouble()))
                .thenReturn(warehouse);
        when(distanceService.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(distance);
    }

    // ── API 2: calculateFromWarehouse ───────────────────────

    @Nested
    class CalculateFromWarehouse {

        @Test
        void shouldUseMiniVanForShortDistance() {
            stubWarehouseLookups(50.0);
            double charge = shippingService.calculateFromWarehouse(
                    1L, 1L, 1L, DeliverySpeed.STANDARD);
            // MiniVan: 50 * 5 * 3 = 750, + base 10 = 760
            assertEquals(760.0, charge);
        }

        @Test
        void shouldUseTruckForMediumDistance() {
            stubWarehouseLookups(200.0);
            double charge = shippingService.calculateFromWarehouse(
                    1L, 1L, 1L, DeliverySpeed.STANDARD);
            // Truck: 200 * 5 * 2 = 2000, + base 10 = 2010
            assertEquals(2010.0, charge);
        }

        @Test
        void shouldUseAirForLongDistance() {
            stubWarehouseLookups(600.0);
            double charge = shippingService.calculateFromWarehouse(
                    1L, 1L, 1L, DeliverySpeed.STANDARD);
            // Air: 600 * 5 * 1 = 3000, + base 10 = 3010
            assertEquals(3010.0, charge);
        }

        @Test
        void expressShouldAddWeightSurcharge() {
            stubWarehouseLookups(50.0);
            double charge = shippingService.calculateFromWarehouse(
                    1L, 1L, 1L, DeliverySpeed.EXPRESS);
            // 10 + (1.2 * 5) + (50 * 5 * 3) = 10 + 6 + 750 = 766
            assertEquals(766.0, charge);
        }

        @Test
        void shouldThrowWhenWarehouseNotFound() {
            when(warehouseRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateFromWarehouse(
                            99L, 1L, 1L, DeliverySpeed.STANDARD));
        }

        @Test
        void shouldThrowWhenCustomerNotFound() {
            when(warehouseRepo.findById(1L)).thenReturn(Optional.of(warehouse));
            when(customerRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateFromWarehouse(
                            1L, 99L, 1L, DeliverySpeed.STANDARD));
        }

        @Test
        void shouldThrowWhenProductNotFound() {
            when(warehouseRepo.findById(1L)).thenReturn(Optional.of(warehouse));
            when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
            when(productRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateFromWarehouse(
                            1L, 1L, 99L, DeliverySpeed.STANDARD));
        }
    }

    // ── API 3: calculateForSeller ───────────────────────────

    @Nested
    class CalculateForSeller {

        @Test
        void shouldReturnChargeAndWarehouseInfo() {
            stubSellerLookups(50.0);
            FullShippingChargeResponse resp =
                    shippingService.calculateForSeller(1L, 1L, DeliverySpeed.STANDARD);

            assertEquals(760.0, resp.shippingCharge());
            assertEquals(1L, resp.nearestWarehouse().warehouseId());
            assertEquals(warehouse.getLatitude(),
                    resp.nearestWarehouse().warehouseLocation().lat());
        }

        @Test
        void shouldApplyExpressSurchargeForSeller() {
            stubSellerLookups(50.0);
            FullShippingChargeResponse resp =
                    shippingService.calculateForSeller(1L, 1L, DeliverySpeed.EXPRESS);
            assertEquals(766.0, resp.shippingCharge());
        }

        @Test
        void shouldThrowWhenSellerNotFound() {
            when(sellerRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateForSeller(
                            99L, 1L, DeliverySpeed.STANDARD));
        }

        @Test
        void shouldThrowWhenNoProductForSeller() {
            when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
            when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
            when(productRepo.findFirstBySellerId(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateForSeller(
                            1L, 1L, DeliverySpeed.STANDARD));
        }

        @Test
        void shouldThrowWhenNoActiveWarehouse() {
            when(sellerRepo.findById(1L)).thenReturn(Optional.of(seller));
            when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
            when(productRepo.findFirstBySellerId(1L)).thenReturn(Optional.of(product));
            when(warehouseService.findNearestToLocation(anyDouble(), anyDouble()))
                    .thenThrow(new ResourceNotFoundException("No active warehouse found"));
            assertThrows(ResourceNotFoundException.class,
                    () -> shippingService.calculateForSeller(
                            1L, 1L, DeliverySpeed.STANDARD));
        }
    }

    // ── Strategy boundary tests ─────────────────────────────

    @Nested
    class StrategyBoundaries {

        @Test
        void exactlyAt100ShouldUseMiniVan() {
            assertInstanceOf(MiniVanStrategy.class,
                    shippingService.resolveStrategy(100.0));
        }

        @Test
        void justAbove100ShouldUseTruck() {
            assertInstanceOf(TruckStrategy.class,
                    shippingService.resolveStrategy(100.1));
        }

        @Test
        void exactlyAt500ShouldUseTruck() {
            assertInstanceOf(TruckStrategy.class,
                    shippingService.resolveStrategy(500.0));
        }

        @Test
        void justAbove500ShouldUseAir() {
            assertInstanceOf(AirStrategy.class,
                    shippingService.resolveStrategy(500.1));
        }
    }
}
