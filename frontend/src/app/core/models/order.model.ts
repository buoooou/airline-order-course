import { User } from './user.model';
import { FlightInfo } from './flight-info.model';

export interface Order {
    id: number;
    orderNumber: string;
    status: string;
    amount: number;
    creationDate: string;
    user?: User;
    flightInfo?: FlightInfo;
}