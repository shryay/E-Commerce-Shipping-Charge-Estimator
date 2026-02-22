package com.ecommerce.shipping.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends BaseEntity {

    private String name;

    private double latitude;

    private double longitude;

    private int capacity;

    private boolean active;
}
