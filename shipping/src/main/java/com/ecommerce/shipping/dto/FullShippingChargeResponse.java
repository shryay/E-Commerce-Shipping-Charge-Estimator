package com.ecommerce.shipping.dto;

public record FullShippingChargeResponse(
        double shippingCharge,
        NearestWarehouseResponse nearestWarehouse
) {
}
