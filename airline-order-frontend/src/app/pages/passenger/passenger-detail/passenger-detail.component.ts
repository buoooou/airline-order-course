import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PassengerService } from '../../../services/passenger.service';
import { Passenger } from '../../../models/passenger.model';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-passenger-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './passenger-detail.component.html',
  styleUrl: './passenger-detail.component.scss'
})
export class PassengerDetailComponent {
  passenger$: Observable<Passenger | undefined>;

  constructor(
    private passengerService: PassengerService,
    private route: ActivatedRoute
  ) {
    const id = this.route.snapshot.paramMap.get('id');
    this.passenger$ = this.passengerService.getPassengerById(id || '');
  }
}