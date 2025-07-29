package airline.enums;

public enum OrderStatus {
    PENDING_PAYMENT {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == PAID || next == CANCELLED;
        }
    },
    PAID {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == TICKETED;
        }
    },
    CANCELLED,
    TICKETED;

    public boolean canTransitionTo(OrderStatus next) {
        return false; // 默认终态
    }
}