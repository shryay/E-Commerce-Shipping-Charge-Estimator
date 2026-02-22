# E-Commerce Shipping Charge Estimator

A B2B e-commerce marketplace API service that intelligently calculates shipping charges based on distance, product weight, and delivery speed preferences. This service supports multiple transportation modes (MiniVan, Truck, Air) with dynamic selection based on distance thresholds, and includes warehouse-to-customer distance calculations using the Haversine formula.

## Features

- **Distance-Based Transport Mode Selection**: Automatically selects optimal transport mode (MiniVan, Truck, Air) based on warehouse-to-customer distance
- **Dynamic Pricing**: Calculates shipping charges considering:
  - Distance between warehouse and customer (using Haversine formula)
  - Product weight
  - Delivery speed (Standard or Express)
  - Transport mode-specific rates
- **Nearest Warehouse Lookup**: Finds the nearest warehouse for a seller and calculates shipping from there
- **Delivery Speed Surcharges**: Applies additional charges for Express delivery
- **Caching**: Optimized performance with caching for frequently requested calculations
- **Swagger/OpenAPI Documentation**: Built-in API documentation

## Technology Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Persistence**: JPA/Hibernate with relational database
- **API Documentation**: Swagger/OpenAPI 3.0
- **Testing**: JUnit 5 with Mockito
- **Build Tool**: Maven

## Project Structure

```
shipping/
├── src/main/java/com/ecommerce/shipping/
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   │   ├── ShippingService.java
│   │   └── strategy/        # Transport mode strategies
│   │       ├── TransportStrategy.java
│   │       ├── MiniVanStrategy.java
│   │       ├── TruckStrategy.java
│   │       └── AirStrategy.java
│   ├── entity/              # Database entities
│   ├── repository/          # Data access layer
│   ├── dto/                 # Data transfer objects
│   ├── exception/           # Custom exceptions
│   └── config/              # Configuration classes
└── src/test/java/          # Unit tests
```

## API Endpoints

### 1. Get Shipping Charge from Specific Warehouse

**GET** `/api/v1/shipping-charge`

Calculates shipping charge for a specific warehouse-to-customer route.

**Parameters:**
- `warehouseId` (Long): ID of the warehouse
- `customerId` (Long): ID of the customer
- `productId` (Long): ID of the product
- `deliverySpeed` (DeliverySpeed): `STANDARD` or `EXPRESS`

**Response:**
```json
{
  "shippingCharge": 45.50
}
```

### 2. Calculate Shipping for Seller with Auto Warehouse Selection

**POST** `/api/v1/shipping-charge/calculate`

End-to-end calculation that finds the nearest warehouse for the seller, then computes shipping charge from that warehouse to the customer.

**Request Body:**
```json
{
  "sellerId": 1,
  "customerId": 1,
  "deliverySpeed": "STANDARD"
}
```

**Response:**
```json
{
  "shippingCharge": 45.50,
  "nearestWarehouse": {
    "warehouseId": 1,
    "warehouseLocation": {
      "lat": 28.7041,
      "lng": 77.1025
    }
  }
}
```

## Shipping Calculation Logic

### Distance Thresholds
- **MiniVan**: Distance ≤ 100 km
- **Truck**: 100 km < Distance ≤ 500 km
- **Air**: Distance > 500 km

### Pricing Formula

1. **Base Charge**: 10.0 (fixed)
2. **Transport Mode Charge**: `distance × weight × rate`
   - MiniVan Rate: 3.0
   - Truck Rate: 2.0
   - Air Rate: 1.0
3. **Express Surcharge** (if applicable):
   - Additional: `1.2 × weight`

### Example Calculation
- Distance: 150 km, Weight: 5 kg, Speed: STANDARD
- Transport Mode: Truck (rate 2.0)
- Charge = 10.0 + (150 × 5 × 2.0) = 10.0 + 1500.0 = 1510.0

## Distance Calculation

The service uses the **Haversine formula** to calculate the great-circle distance between warehouse and customer coordinates:

```
a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
c = 2 ⋅ atan2(√a, √(1−a))
d = R ⋅ c
```

Where:
- φ is latitude, λ is longitude, R is earth's radius (~6371 km)

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Database (configured in application properties)

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

### Running Tests

```bash
mvn test
```

## API Documentation

Once the application is running, access the Swagger UI documentation at:

```
http://localhost:8080/swagger-ui.html
```

## Design Patterns Used

- **Strategy Pattern**: Transport mode selection via `TransportStrategy` interface
- **Factory Pattern**: Strategy implementations (MiniVan, Truck, Air)
- **Service Layer Pattern**: Business logic separation
- **Data Transfer Object (DTO)**: Request/response decoupling
- **Repository Pattern**: Data access abstraction

## Configuration

Key application properties (configure as needed):

```properties
spring.application.name=shipping
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/shipping
spring.datasource.username=root
spring.datasource.password=password
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK`: Successful calculation
- `400 Bad Request`: Invalid or missing parameters
- `404 Not Found`: Warehouse, customer, or product not found
- `500 Internal Server Error`: Server-side errors

## Performance Optimization

- **Caching**: Shipping charges are cached with key format: `warehouseId-customerId-productId-speed`
- **Lazy Loading**: Database queries optimized using JPA relationships

## Contributing

1. Create a feature branch
2. Commit your changes
3. Push to the branch
4. Open a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

**Shipping Team**  
Email: shreya.palit@jumbotail.com

---

**Version**: 1.0.0  
**Last Updated**: February 2026