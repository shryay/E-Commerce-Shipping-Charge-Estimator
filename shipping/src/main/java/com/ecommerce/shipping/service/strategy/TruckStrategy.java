package com.ecommerce.shipping.service.strategy;

import org.springframework.stereotype.Component;

@Component("TRUCK")
public class TruckStrategy implements TransportStrategy {

    private static final double RATE = 2.0;

    @Override
    public double calculate(double distance, double weight) {
        return distance * weight * RATE;
    }
}
