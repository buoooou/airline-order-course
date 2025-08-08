-- 创建订单状态历史记录表
CREATE TABLE order_state_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_number VARCHAR(250) NOT NULL,
    from_state VARCHAR(50) NOT NULL,
    to_state VARCHAR(50),
    event VARCHAR(50) NOT NULL,
    operator VARCHAR(100),
    operator_role VARCHAR(50),
    success BOOLEAN NOT NULL,
    error_message VARCHAR(1000),
    request_data VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 添加索引以提高查询性能
    INDEX idx_order_id (order_id),
    INDEX idx_order_number (order_number),
    INDEX idx_success (success),
    INDEX idx_created_at (created_at),
    INDEX idx_order_id_success (order_id, success)
);

-- 添加外键约束（可选，如果需要在删除订单时级联删除历史记录）
-- ALTER TABLE order_state_history 
-- ADD CONSTRAINT fk_order_state_history_order 
-- FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;