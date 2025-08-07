/**
 * 订单状态枚举
 */
export enum OrderStatus {
  PENDING_PAYMENT = 'PENDING_PAYMENT',
  PAID = 'PAID',
  TICKETING_IN_PROGRESS = 'TICKETING_IN_PROGRESS',
  TICKETING_FAILED = 'TICKETING_FAILED',
  TICKETED = 'TICKETED',
  CANCELLED = 'CANCELLED'
}

/**
 * 订单状态中文映射
 */
export const OrderStatusLabels = {
  [OrderStatus.PENDING_PAYMENT]: '待付款',
  [OrderStatus.PAID]: '已付款',
  [OrderStatus.TICKETING_IN_PROGRESS]: '出票中',
  [OrderStatus.TICKETING_FAILED]: '出票失败',
  [OrderStatus.TICKETED]: '已出票',
  [OrderStatus.CANCELLED]: '已取消'
};

/**
 * 订单模型
 */
export interface Order {
  id: number;
  orderNumber: string;
  status: OrderStatus;
  statusDescription?: string;
  amount: number;
  creationDate: string;
  lastUpdated?: string;
  userId: number;
  username?: string;
  flightInfo?: FlightInfo;
  
  // 航班相关字段（从flightInfo中提取的便捷字段）
  flightNumber?: string;
  airline?: string;
  departureAirport?: string;
  arrivalAirport?: string;
  departureTime?: string;
  arrivalTime?: string;
  
  // 乘客信息
  passengerNames?: string;
  passengerCount?: number;
  contactPhone?: string;
  contactEmail?: string;
  
  // 时间线相关字段
  paymentTime?: string;
  ticketingStartTime?: string;
  ticketingCompletionTime?: string;
  ticketingFailureReason?: string;
  cancellationTime?: string;
  cancellationReason?: string;
}

/**
 * 航班信息模型
 */
export interface FlightInfo {
  id: number;
  flightNumber: string;
  airline: string;
  departureAirport: string;
  arrivalAirport: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  orderId: number;
}

/**
 * 订单状态更新请求
 */
export interface OrderStatusUpdateRequest {
  status: OrderStatus;
}

/**
 * 订单查询参数
 */
export interface OrderQueryParams {
  page?: number;
  size?: number;
  status?: OrderStatus;
  orderNumber?: string;
}
