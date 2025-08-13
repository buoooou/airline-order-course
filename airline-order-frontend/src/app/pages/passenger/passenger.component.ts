import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-passenger',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './passenger.component.html',
  styleUrl: './passenger.component.scss'
})
export class PassengerComponent {}