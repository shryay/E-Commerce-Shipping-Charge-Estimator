package com.ecommerce.shipping.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Seller extends BaseEntity {

    private String name;

    private String gstNumber;

    private double rating;

    private double latitude;

    private double longitude;

}
