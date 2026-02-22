package com.ecommerce.shipping.dto;

import com.ecommerce.shipping.entity.enums.DeliverySpeed;
import jakarta.validation.constraints.NotNull;

public record ShippingCalculateRequest(

        @NotNull(message = "sellerId is required")
        Long sellerId,

        @NotNull(message = "customerId is required")
        Long customerId,

        @NotNull(message = "deliverySpeed is required")
        DeliverySpeed deliverySpeed
) {
}
