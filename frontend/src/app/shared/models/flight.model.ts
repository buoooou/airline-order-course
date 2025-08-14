export interface Airport {
  id: number;
  code: string;
  name: string;
  city: string;
  country?: string;
}

export interface Airline {
  id: number;
  code: string;
  name: string;
  logoUrl?: string;
}

export interface Flight {
  id: number;
  flightNumber: string;
  airline: Airline;
  departureAirport: Airport;
  arrivalAirport: Airport;
  departureTime: string;
  arrivalTime: string;
  duration: number; // 飞行时长（分钟）
  totalSeats: number;
  availableSeats: number;
  price: number; // 默认价格（通常为经济舱价格）
  economyPrice: number;
  businessPrice: number;
  firstPrice: number;
  status: FlightStatus;
  aircraftType?: string;
}

export enum FlightStatus {
  SCHEDULED = 'SCHEDULED',
  BOARDING = 'BOARDING',
  DEPARTED = 'DEPARTED',
  ARRIVED = 'ARRIVED',
  DELAYED = 'DELAYED',
  CANCELLED = 'CANCELLED'
}

export enum SeatClass {
  ECONOMY = 'ECONOMY',
  BUSINESS = 'BUSINESS',
  FIRST = 'FIRST'
}

export interface SeatClassOption {
  value: SeatClass;
  label: string;
  priceField: 'economyPrice' | 'businessPrice' | 'firstPrice';
}

export interface FlightSearchCriteria {
  departureAirportCode: string;
  arrivalAirportCode: string;
  departureDate: string; // YYYY-MM-DD format
  returnDate?: string; // for round trip
  passengers: number;
  tripType?: 'ONE_WAY' | 'ROUND_TRIP';
}

export interface FlightSearchResult {
  content: Flight[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  error?: string;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}