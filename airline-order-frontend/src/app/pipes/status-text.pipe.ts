import { Pipe, PipeTransform } from '@angular/core';
import { Order } from '../models/order.model';

@Pipe({
  name: 'statusText',
  standalone: false
})
export class StatusTextPipe implements PipeTransform {
  transform(status: Order['status']): string {
    switch (status) {
      case 'PENDING_PAYMENT':
        return '待支付';
      case 'PAID':
        return '已支付';
      case 'PAYMENT_FAILED':
        return '支付失败';
      case 'TICKETING_IN_PROGRESS':
        return '出票中';
      case 'TICKETING_FAILED':
        return '出票失败';
      case 'TICKETED':
        return '已出票';
      case 'CANCELLED':
        return '已取消';
      default:
        return status;
    }
  }
}