export type OrderStatus = 'PENDING_PAYMENT' | 'PAID' | 'TICKETING_IN_PROGRESS' | 'TICKETING_FAILED' | 'TICKETED' | 'CANCELLED' | 'PAYMENT_FAILED';

export interface Passenger {
  id: number;
  name: string;
  phone: string;
}

export interface User {
  id: number;
  username: string;
  role: string;
}

export interface Passenger {
  id: number;
  name: string;
  phone: string;
}

export interface User {
  id: number;
  username: string;
  role: string;
}

export interface Order {
  id: string;
  orderId: string;
  orderNumber: string;
  status: OrderStatus;
  amount: number;
  creationDate: string;
  updateDate: string;
  user: User;
  passenger?: Passenger;
  passengerName?: string;
  passengerPhone?: string;
  flightNumber?: string;
}