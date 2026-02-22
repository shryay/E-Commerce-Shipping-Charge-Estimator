package com.ecommerce.shipping.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends BaseEntity {

    private String name;
    private String phone;
    private String address;
    private String gstNumber;

    private double latitude;
    private double longitude;
}
