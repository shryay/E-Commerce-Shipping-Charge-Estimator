package com.ecommerce.shipping.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shippingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shipping Charge Estimator API")
                        .description("""
                                B2B e-commerce marketplace API for calculating shipping charges.
                                Supports nearest-warehouse lookup, distance-based transport mode
                                selection (MiniVan / Truck / Air), and Standard vs Express delivery speeds.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shipping Team")
                                .email("shreya.palit@jumbotail.com")));
    }
}
