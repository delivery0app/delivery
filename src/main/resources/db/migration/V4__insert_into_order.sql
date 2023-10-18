INSERT INTO order_table (sender_address, delivery_address, weight, description, payment_method, delivery_date,
                         order_status, creation_date, fragile_cargo, price, courier_id, customer_id)
VALUES ('10 Baker Street, Moscow', '20 Oxford Street, Moscow', 10, 'Books', 'CASH', '2023-11-14', 'NEW', NOW(), false,
        500.00, 1, 1),
       ('25 Cambridge Street, Moscow', '35 Harvard Street, Moscow', 5, 'Clothes', 'CARD', '2023-11-15',
        'DELIVERED', NOW(), false, 300.00, 2, 2),
       ('55 Elm Street, Moscow', '150 Park Street, Moscow', 15, 'Grocery', 'CASH', '2023-11-16', 'IN_PROGRESS', NOW(), true,
        650.00, 1, 2),
       ('12 Pine Road, Moscow', '78 Cedar Avenue, Moscow', 20, 'Electronics', 'CARD', '2023-11-17', 'CANCELED',
        NOW(), true, 800.00, 2, 1),
       ('33 Ash Lane, Moscow', '47 Walnut Way, Moscow', 25, 'Office Supplies', 'CASH', '2023-11-18', 'CANCELED', NOW(),
        false, 700.00, 1, 1);