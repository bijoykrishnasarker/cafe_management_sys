CREATE DATABASE cafe_management;
USE cafe_management;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

select * from orders;

CREATE TABLE order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    item_name VARCHAR(100),
    quantity INT,
    price DECIMAL(10, 2),
    total DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

select * from order_items;

CREATE TABLE menu_items (
    item_name VARCHAR(100) PRIMARY KEY,
    price DECIMAL(10, 2) NOT NULL
);

select * from menu_items;

-- Get all order items where quantity is more than 2.
SELECT * FROM order_items
WHERE quantity > 2;

-- Find all items that contain the letter 'e' in their name.
SELECT * FROM menu_items
WHERE item_name LIKE '%e%';

-- Find the total sales (sum of totals) from all orders

SELECT SUM(total) AS total_sales FROM order_items;

-- Find the average price of items sold.
SELECT AVG(price) AS average_price FROM order_items;

-- Get order details with item names and prices.

SELECT o.order_id, o.order_date, i.item_name, i.quantity, i.price, i.total
FROM orders o
JOIN order_items i ON o.order_id = i.order_id;

-- Subquery
-- Get orders that include at least one item costing more than the average item price.

SELECT * FROM order_items
WHERE price > (
    SELECT AVG(price) FROM order_items
);

-- Get all items with total between 100 and 300.
SELECT * FROM order_items
WHERE total BETWEEN 100 AND 300;

-- Multi-Table Join with Aggregation
-- Get total earnings per order (by order_id)

SELECT o.order_id, o.order_date, SUM(i.total) AS total_earned
FROM orders o
JOIN order_items i ON o.order_id = i.order_id
GROUP BY o.order_id, o.order_date
ORDER BY total_earned DESC;

-- Show all items from orders that had more than 300 total value.
SELECT * FROM order_items
WHERE order_id IN (
    SELECT order_id FROM order_items
    GROUP BY order_id
    HAVING SUM(total) > 300
);

-- Show all item names in uppercase
SELECT UPPER(item_name) AS item_uppercase FROM menu_items;

-- Show the length of each item name.
SELECT item_name, LENGTH(item_name) AS name_length FROM menu_items;

-- Show total prices rounded to 1 decimal place.

SELECT item_name, ROUND(total, 1) AS rounded_total
FROM order_items;

-- What are the highest and lowest priced items?

SELECT MAX(price) AS highest_price, MIN(price) AS lowest_price
FROM menu_items;
commit;