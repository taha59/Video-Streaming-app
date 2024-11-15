import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  private readonly router: Router = inject(Router)
  constructor(){
    this.router.navigateByUrl("/featured")
  }
  ngOnInit(): void {
    
  }
  
}
