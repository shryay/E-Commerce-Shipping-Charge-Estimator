package com.ecommerce.shipping.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceServiceTest {

    private final DistanceService distanceService = new DistanceService();

    @Test
    void shouldReturnZeroForSamePoint() {
        double distance = distanceService.calculate(12.97, 77.59, 12.97, 77.59);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void shouldCalculateKnownDistanceBangaloreToChennai() {
        // Bangalore to Chennai ≈ 290 km
        double distance = distanceService.calculate(12.97, 77.59, 13.08, 80.27);
        assertEquals(290.0, distance, 15.0);
    }

    @Test
    void shouldBeSymmetric() {
        double d1 = distanceService.calculate(12.97, 77.59, 13.08, 80.27);
        double d2 = distanceService.calculate(13.08, 80.27, 12.97, 77.59);
        assertEquals(d1, d2, 0.001);
    }

    @Test
    void shouldHandleLargeDistances() {
        // North pole to south pole ≈ 20015 km
        double distance = distanceService.calculate(90, 0, -90, 0);
        assertEquals(20015.0, distance, 100.0);
    }

    @Test
    void shouldHandleEquatorialDistance() {
        // Two points on equator 1 degree apart ≈ 111 km
        double distance = distanceService.calculate(0, 0, 0, 1);
        assertEquals(111.0, distance, 2.0);
    }
}
