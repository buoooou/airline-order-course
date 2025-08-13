export interface OrderDto {
  id: number;
  orderNumber: string;
  status: OrderStatus;
  amount: number;
  createdDate: string;
  userId: number;
  flightInfoId: number;
}

export interface CreateOrderRequest {
  flightInfoId: number;
  amount: number;
}

export enum OrderStatus {
  PENDING_PAYMENT = 'PENDING_PAYMENT',
  PAID = 'PAID',
  TICKETING_IN_PROGRESS = 'TICKETING_IN_PROGRESS',
  TICKETED = 'TICKETED',
  TICKETING_IN_FAILED = 'TICKETING_IN_FAILED',
  CANCELED = 'CANCELED'
}
