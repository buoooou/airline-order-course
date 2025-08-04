package com.postion.airlineorderbackend.statemachine;

public enum OrderStatus {
    PENDING {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == CONFIRMED || newStatus == CANCELLED;
        }
    },
    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PROCESSING || newStatus == CANCELLED;
        }
    },
    PROCESSING {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == SHIPPED || newStatus == FAILED;
        }
    },
    SHIPPED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == DELIVERED || newStatus == FAILED;
        }
    },
    DELIVERED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == REFUNDED;
        }
    },
    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return false;
        }
    },
    REFUNDED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return false;
        }
    },
    FAILED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == REFUNDED;
        }
    };

    public abstract boolean canTransitionTo(OrderStatus newStatus);
}