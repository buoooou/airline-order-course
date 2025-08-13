import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PassengerService } from '../../../services/passenger.service';
import { Passenger } from '../../../models/passenger.model';
import { Observable } from 'rxjs';
import { MatTableModule } from '@angular/material/table';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-passenger-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatTableModule],
  templateUrl: './passenger-list.component.html',
  styleUrl: './passenger-list.component.scss'
})
export class PassengerListComponent {
  passengers: Passenger[] = [];
  dataSource = new MatTableDataSource<Passenger>();

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'pending':
        return 'status-pending';
      case 'confirmed':
        return 'status-confirmed';
      case 'cancelled':
        return 'status-cancelled';
      default:
        return '';
    }
  }
  passengers$: Observable<{ passengers: Passenger[]; total: number; }>;

  constructor(private passengerService: PassengerService) {
    this.passengers$ = this.passengerService.getPassengers();
  }
}