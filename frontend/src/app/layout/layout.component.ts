import { Component } from '@angular/core';
import { HeaderComponent } from "../components/header/header.component";
import { MainContentComponent } from "../components/main-content/main-content.component";

@Component({
  selector: 'app-layout',
  template: `
    <app-header></app-header>
    <app-main-content></app-main-content>
  `,
  styles: [],
  imports: [HeaderComponent, MainContentComponent]
})
export class LayoutComponent {}