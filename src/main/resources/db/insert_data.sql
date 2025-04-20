TRUNCATE public.sku;

INSERT INTO sku (sku_id, name, width, height, length, weight, shape)
VALUES (1, 'A SKU', 3, 3, 1, 1, 'BOX'),
       (2, 'B SKU', 3, 1, 1, 1, 'BOX'),
       (3, 'C SKU', 3, 1, 1, 1, 'BOX'),
       (4, 'D SKU', 3, 1, 1, 1, 'BOX'),
       (5, 'E SKU', 1, 1, 3, 1, 'BOX'),
       (6, 'F SKU', 1, 3, 1, 1, 'BOX'),
       (7, 'G SKU', 3, 1, 1, 1, 'BOX');

TRUNCATE box;

INSERT INTO box (box_id, name, width, height, length, weight, max_weight)
VALUES (1, 'Q BOX', 3, 3, 3, 1, 10),
       (2, 'W BOX', 3, 3, 3, 1, 10);