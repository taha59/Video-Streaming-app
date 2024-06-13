import { Component, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})


export class HeaderComponent implements OnInit{

  isAuthenticated: boolean = false
  constructor(private oidcSecurityService: OidcSecurityService){}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
     ({isAuthenticated}) => {
      // console.log("this.")
      
      this.isAuthenticated = isAuthenticated
      // this.isAuthenticated = true
     }
    )
  }

  login(){
    console.log("loggin in..")
    this.oidcSecurityService.authorize()
  }

  logout(){
    
    this.oidcSecurityService.logoffAndRevokeTokens()
    
    // console.log("loggin out..", this.isAuthenticated)
  }
}
