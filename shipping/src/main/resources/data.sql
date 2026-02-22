-- Sellers
INSERT INTO seller (id, name, gst_number, rating, latitude, longitude, created_at, updated_at) VALUES
(1, 'Nestle Seller', 'GST29AAA0001', 4.5, 12.9716, 77.5946, NOW(), NOW()),
(2, 'Rice Seller', 'GST27BBB0002', 4.2, 19.0760, 72.8777, NOW(), NOW()),
(3, 'Sugar Seller', 'GST07CCC0003', 4.0, 28.6139, 77.2090, NOW(), NOW());

-- Products
INSERT INTO product (id, name, price, weight_kg, length_cm, width_cm, height_cm, seller_id, created_at, updated_at) VALUES
(1, 'Maggie 500g Packet', 10, 0.5, 10, 10, 10, 1, NOW(), NOW()),
(2, 'Rice Bag 10Kg', 500, 10, 100, 80, 50, 2, NOW(), NOW()),
(3, 'Sugar Bag 25Kg', 700, 25, 100, 90, 60, 3, NOW(), NOW());

-- Customers (Kirana Stores)
INSERT INTO customer (id, name, phone, address, gst_number, latitude, longitude, created_at, updated_at) VALUES
(1, 'Shree Kirana Store', '9847000001', 'Jayanagar, Bangalore', 'CUST29X0001', 11.232, 23.445495, NOW(), NOW()),
(2, 'Andheri Mini Mart', '9101000002', 'Andheri West, Mumbai', 'CUST27X0002', 17.232, 33.445495, NOW(), NOW());

-- Warehouses
INSERT INTO warehouse (id, name, latitude, longitude, capacity, active, created_at, updated_at) VALUES
(1, 'BLR_Warehouse', 12.99999, 37.923273, 1000, true, NOW(), NOW()),
(2, 'MUMB_Warehouse', 11.99999, 27.923273, 800, true, NOW(), NOW());
