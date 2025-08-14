export interface Passenger {
  id?: number;
  name: string;
  idNumber: string;
  phone: string;
  email: string;
  dateOfBirth: Date;
  gender: 'MALE' | 'FEMALE';
}

export interface Order {
  id: number;
  orderNumber: string;
  flight: Flight;
  passengers: Passenger[];
  totalAmount: number;
  status: OrderStatus;
  paymentMethod?: PaymentMethod;
  bookingDate: string;
  paymentDate?: string;
  userId: number;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PAID = 'PAID',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  ALIPAY = 'ALIPAY',
  WECHAT = 'WECHAT',
  BANK_TRANSFER = 'BANK_TRANSFER'
}

export interface BookingRequest {
  flightId: number;
  passengers: Passenger[];
  contactName: string;
  contactPhone: string;
  contactEmail: string;
  paymentMethod: PaymentMethod;
  totalAmount: number;
  seatClass: string;
}

export interface PaymentRequest {
  orderId: number;
  paymentMethod: PaymentMethod;
  amount: number;
}

// Import Flight interface
import { Flight } from './flight.model';