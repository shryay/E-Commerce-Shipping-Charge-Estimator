package com.ecommerce.shipping.service.strategy;

import org.springframework.stereotype.Component;

@Component("MINIVAN")
public class MiniVanStrategy implements TransportStrategy {

    private static final double RATE = 3.0; // Rs per km per kg

    @Override
    public double calculate(double distance, double weight) {
        return distance * weight * RATE;
    }
}
