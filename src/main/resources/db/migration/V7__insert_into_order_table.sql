INSERT INTO order_table (sender_address, delivery_address, weight, description, payment_method, delivery_date,
                         order_status, creation_date, fragile_cargo, price, courier_id, customer_id)
VALUES ('Moscow', 'Paris', 7, 'Cargo description', 'CARD', CURRENT_DATE + 10, 'NEW', CURRENT_TIMESTAMP, FALSE, 50.00, 1,
        1);