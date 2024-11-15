import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../user.service';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css'
})
export class CallbackComponent implements OnInit{
  private readonly userService = inject(UserService)
  private readonly oidcSecurityService = inject(OidcSecurityService)

  constructor(){
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({isAuthenticated}) => {
        if(isAuthenticated){
        this.userService.registerUser()
        }
      }
     )
  }
}
