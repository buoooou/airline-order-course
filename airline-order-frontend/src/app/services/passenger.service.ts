import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Passenger } from '../models/passenger.model';

@Injectable({
  providedIn: 'root'
})
export class PassengerService {
  private maskPhoneNumber(phone: string): string {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  }
  private passengers: Passenger[] = [
    { id: 'PASS-123', name: '张三', contact: '13800138000' },
    { id: 'PASS-456', name: '李四', contact: '13900139000' },
  ];

  getPassengers(page: number = 1, pageSize: number = 10): Observable<{ passengers: Passenger[], total: number }> {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedPassengers = this.passengers.slice(startIndex, endIndex).map(passenger => ({
      ...passenger,
      contact: this.maskPhoneNumber(passenger.contact)
    }));
    return of({ passengers: paginatedPassengers, total: this.passengers.length });
  }

  getPassengerById(id: string): Observable<Passenger | undefined> {
    const passenger = this.passengers.find(p => p.id === id);
    if (passenger) {
      passenger.contact = this.maskPhoneNumber(passenger.contact);
    }
    return of(passenger);
  }
}