import { Routes } from '@angular/router';
import { SearchResultsComponent } from './search-results/search-results.component';
import { FlightDetailsComponent } from './flight-details/flight-details.component';
import { BookingComponent } from './booking/booking.component';
// import { PlaceholderComponent } from '../../shared/components/placeholder/placeholder.component';

export const flightsRoutes: Routes = [
  // {
  //   path: '',
  //   component: PlaceholderComponent,
  //   data: { title: 'Flights' }
  // },
  {
    path: 'search',
    component: SearchResultsComponent,
    data: { title: 'Search Results' }
  },
  {
    path: 'results',
    component: SearchResultsComponent,
    data: { title: 'Flight Results' }
  },
  {
    path: 'details/:id',
    component: FlightDetailsComponent,
    data: { title: 'Flight Details' }
  },
  {
    path: 'booking/:id',
    component: BookingComponent,
    data: { title: 'Book Flight' }
  }
];