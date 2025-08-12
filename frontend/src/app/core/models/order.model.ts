export interface OrderDto {
  id: number;
  orderNumber: string;
  status: string;
  amount: number;
  creationDate: string;
  userId: number;
  flightId: number;
}

export interface ApiResponse<T> {
  result: string;
  message: string;
  data: T;
}