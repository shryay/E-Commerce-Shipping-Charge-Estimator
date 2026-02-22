package com.ecommerce.shipping.service.strategy;

import org.springframework.stereotype.Component;

@Component("AIR")
public class AirStrategy implements TransportStrategy {

    private static final double RATE = 1.0;

    @Override
    public double calculate(double distance, double weight) {
        return distance * weight * RATE;
    }
}
