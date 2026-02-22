package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.LocationDto;
import com.ecommerce.shipping.dto.NearestWarehouseResponse;
import com.ecommerce.shipping.entity.Warehouse;
import com.ecommerce.shipping.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Tag(name = "Warehouse", description = "Warehouse lookup operations")
public class WarehouseController {

    private final WarehouseService service;

    @Operation(
            summary = "Get nearest warehouse for a seller",
            description = "Given a seller and product, returns the nearest active warehouse "
                    + "where the seller can drop off the product.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nearest warehouse found"),
                    @ApiResponse(responseCode = "400", description = "Product does not belong to seller"),
                    @ApiResponse(responseCode = "404", description = "Seller, product, or warehouse not found")
            })
    @GetMapping("/nearest")
    public NearestWarehouseResponse nearest(
            @Parameter(description = "ID of the seller", example = "1")
            @RequestParam Long sellerId,
            @Parameter(description = "ID of the product to be shipped", example = "1")
            @RequestParam Long productId) {

        Warehouse warehouse = service.findNearest(sellerId, productId);
        return new NearestWarehouseResponse(
                warehouse.getId(),
                new LocationDto(warehouse.getLatitude(), warehouse.getLongitude())
        );
    }
}
