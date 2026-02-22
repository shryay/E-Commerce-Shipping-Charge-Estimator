package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.FullShippingChargeResponse;
import com.ecommerce.shipping.dto.ShippingCalculateRequest;
import com.ecommerce.shipping.dto.ShippingChargeResponse;
import com.ecommerce.shipping.entity.enums.DeliverySpeed;
import com.ecommerce.shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping-charge")
@RequiredArgsConstructor
@Tag(name = "Shipping Charge", description = "Shipping charge calculation endpoints")
public class ShippingController {

    private final ShippingService service;

    @Operation(
            summary = "Get shipping charge from a warehouse to a customer",
            description = "Calculates the shipping charge based on the distance between the "
                    + "warehouse and customer, the product weight, and the delivery speed.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Shipping charge calculated"),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing parameters"),
                    @ApiResponse(responseCode = "404", description = "Warehouse, customer, or product not found")
            })
    @GetMapping
    public ShippingChargeResponse getShippingCharge(
            @Parameter(description = "ID of the warehouse", example = "1")
            @RequestParam Long warehouseId,
            @Parameter(description = "ID of the customer", example = "1")
            @RequestParam Long customerId,
            @Parameter(description = "ID of the product", example = "1")
            @RequestParam Long productId,
            @Parameter(description = "Delivery speed: STANDARD or EXPRESS", example = "STANDARD")
            @RequestParam DeliverySpeed deliverySpeed) {

        double charge = service.calculateFromWarehouse(
                warehouseId, customerId, productId, deliverySpeed);
        return new ShippingChargeResponse(charge);
    }

    @Operation(
            summary = "Calculate shipping charges for a seller and customer",
            description = "End-to-end calculation: finds the nearest warehouse for the seller, "
                    + "then computes the shipping charge from that warehouse to the customer. "
                    + "Returns both the charge and the warehouse details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Shipping charge and warehouse returned"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "404", description = "Seller, customer, product, or warehouse not found")
            })
    @PostMapping("/calculate")
    public FullShippingChargeResponse calculate(
            @Valid @RequestBody ShippingCalculateRequest request) {

        return service.calculateForSeller(
                request.sellerId(),
                request.customerId(),
                request.deliverySpeed());
    }
}
