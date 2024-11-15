import { Component, OnInit, inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})


export class HeaderComponent implements OnInit{

  private readonly oidcSecurityService = inject(OidcSecurityService)
  isAuthenticated: boolean = false

  constructor(){}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
     ({isAuthenticated}) => {
      this.isAuthenticated = isAuthenticated
     }
    )
  }

  login(){
    console.log("trying to log in")
    this.oidcSecurityService.authorize()
  }

  logoff(){

    this.oidcSecurityService.logoffAndRevokeTokens()
    .subscribe(() => {
      this.oidcSecurityService.logoffLocal();
    });

  }
}
