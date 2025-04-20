TRUNCATE TABLE sku;

INSERT INTO sku (sku_id, name, width, height, length, weight, shape, created_at, updated_at)
VALUES (1, 'A SKU', 3, 3, 1, 1, 'BOX', current_timestamp, current_timestamp),
       (2, 'B SKU', 3, 1, 1, 1, 'BOX', current_timestamp, current_timestamp),
       (3, 'C SKU', 3, 1, 1, 1, 'BOX', current_timestamp, current_timestamp),
       (4, 'D SKU', 3, 1, 1, 1, 'BOX', current_timestamp, current_timestamp),
       (5, 'E SKU', 1, 1, 3, 1, 'BOX', current_timestamp, current_timestamp),
       (6, 'F SKU', 1, 3, 1, 1, 'BOX', current_timestamp, current_timestamp),
       (7, 'G SKU', 3, 1, 1, 1, 'BOX', current_timestamp, current_timestamp);

TRUNCATE TABLE box;

INSERT INTO box (box_id, name, width, height, length, weight, max_weight, created_at, updated_at)
VALUES (1, 'Q BOX', 3, 3, 3, 1, 10, current_timestamp, current_timestamp),
       (2, 'W BOX', 3, 3, 3, 1, 10, current_timestamp, current_timestamp);