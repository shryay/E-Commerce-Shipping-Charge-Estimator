package com.ecommerce.shipping.service.strategy;

public interface TransportStrategy {

    /**
     * Calculates shipping charge based on distance and weight.
     *
     * @param distance in kilometers
     * @param weight in kilograms
     * @return calculated base shipping charge
     */
    double calculate(double distance, double weight);

}
