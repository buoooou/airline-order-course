import {User} from './user.model';

export interface Order {
    id: number;
    orderNumber: string;
    status:
        | 'NONE'
        | 'PENDING_PAYMENT'
        | 'PAID'
        | 'TICKETING_IN_PROGRESS'
        | 'TICKETING_FAILED'
        | 'TICKETED'
        | 'CANCELED';
    amount: number;
    createTime: Date;
    updateTime: Date;
    user: User;
    flightInfo?: any; // 航班信息
}

// export interface ApiResponseDTO<T> {
//     code: number;
//     message: string;
//     data: T;
// }