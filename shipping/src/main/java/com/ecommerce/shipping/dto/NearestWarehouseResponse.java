package com.ecommerce.shipping.dto;

public record NearestWarehouseResponse(
        Long warehouseId,
        LocationDto warehouseLocation
) {
}
