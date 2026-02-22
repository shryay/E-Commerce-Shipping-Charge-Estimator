package com.ecommerce.shipping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends BaseEntity {

    private String name;

    private double price;

    private double weightKg;

    private double lengthCm;

    private double widthCm;

    private double heightCm;

    @ManyToOne
    private Seller seller;
}
