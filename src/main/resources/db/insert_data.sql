-- SKU, BOX 데이터 삭제 및 재생성
TRUNCATE TABLE sku CASCADE;
TRUNCATE TABLE box CASCADE;

-- SKU 데이터 추가 (다양한 크기와 모양으로 업데이트)
INSERT INTO sku (sku_id, name, width, height, length, weight, shape, created_at, updated_at)
VALUES
    -- BOX 형태 상품들
    (1, 'Small Book', 15, 20, 3, 500, 'BOX', current_timestamp, current_timestamp),
    (2, 'Medium Box', 30, 30, 30, 1500, 'BOX', current_timestamp, current_timestamp),
    (3, 'Thin Board', 50, 50, 5, 800, 'BOX', current_timestamp, current_timestamp),
    (4, 'Large Panel', 100, 100, 10, 5000, 'BOX', current_timestamp, current_timestamp),

    -- CYLINDER 형태 상품들
    (5, 'Paper Roll', 5, 5, 100, 1200, 'CYLINDER', current_timestamp, current_timestamp),
    (6, 'Bottle', 8, 8, 25, 600, 'CYLINDER', current_timestamp, current_timestamp),

    -- CONE 형태 상품들
    (7, 'Ice Cream Cone', 10, 10, 15, 100, 'CONE', current_timestamp, current_timestamp),

    -- POUCH 형태 상품들
    (8, 'Food Pack', 30, 20, 10, 800, 'POUCH', current_timestamp, current_timestamp),
    (9, 'Small Pouch', 15, 15, 5, 300, 'POUCH', current_timestamp, current_timestamp);

-- BOX 데이터 추가 (다양한 크기의 박스들)
INSERT INTO box (box_id, name, width, height, length, weight, max_weight, created_at, updated_at)
VALUES
    -- 작은 박스들
    (1, 'Small Box', 40, 40, 40, 100, 3000, current_timestamp, current_timestamp),
    (2, 'Compact Box', 50, 30, 60, 150, 5000, current_timestamp, current_timestamp),

    -- 중간 크기 박스들
    (3, 'Medium Box', 60, 60, 60, 200, 8000, current_timestamp, current_timestamp),
    (4, 'Standard Box', 80, 50, 70, 250, 10000, current_timestamp, current_timestamp),

    -- 큰 박스들
    (5, 'Large Box', 100, 100, 100, 300, 15000, current_timestamp, current_timestamp),
    (6, 'Extra Large Box', 120, 120, 120, 400, 20000, current_timestamp, current_timestamp);

-- Order 데이터 추가 (다양한 테스트 시나리오)
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE order_sku CASCADE;

INSERT INTO orders (order_id, title, created_at, updated_at)
VALUES
    -- 적은 아이템 주문
    (1, 'Small Order - Test Scenario 1', current_timestamp, current_timestamp),

    -- 중간 아이템 주문
    (2, 'Regular Order - Mixed Items', current_timestamp, current_timestamp),

    -- 많은 아이템 주문 (박스 여러개 필요)
    (3, 'Large Order - Multiple Boxes', current_timestamp, current_timestamp),

    -- 특정 형태 집중 주문
    (4, 'Cylindrical Items Only', current_timestamp, current_timestamp),

    -- 무거운 아이템 포함 주문
    (5, 'Heavy Items - Weight Challenge', current_timestamp, current_timestamp),

    -- 이상적인 피팅 테스트
    (6, 'Perfect Fit Test', current_timestamp, current_timestamp);

-- OrderSku 데이터 추가
INSERT INTO order_sku (order_sku_id, order_id, sku_id, requested_quantity, created_at, updated_at)
VALUES
    -- Order 1: 작은 주문 (2개 SKU만)
    (1, 1, 1, 2, current_timestamp, current_timestamp),
    (2, 1, 6, 1, current_timestamp, current_timestamp),

    -- Order 2: 일반 주문 (다양한 아이템)
    (3, 2, 1, 5, current_timestamp, current_timestamp),
    (4, 2, 2, 3, current_timestamp, current_timestamp),
    (5, 2, 8, 2, current_timestamp, current_timestamp),

    -- Order 3: 대량 주문 (여러 박스 필요)
    (6, 3, 2, 10, current_timestamp, current_timestamp),
    (7, 3, 3, 8, current_timestamp, current_timestamp),
    (8, 3, 4, 5, current_timestamp, current_timestamp),
    (9, 3, 5, 12, current_timestamp, current_timestamp),

    -- Order 4: 원통형 아이템만
    (10, 4, 5, 15, current_timestamp, current_timestamp),
    (11, 4, 6, 20, current_timestamp, current_timestamp),

    -- Order 5: 무거운 아이템 포함
    (12, 5, 4, 6, current_timestamp, current_timestamp),
    (13, 5, 2, 4, current_timestamp, current_timestamp),
    (14, 5, 3, 3, current_timestamp, current_timestamp),

    -- Order 6: 이상적인 피팅 테스트
    (15, 6, 1, 27, current_timestamp, current_timestamp);