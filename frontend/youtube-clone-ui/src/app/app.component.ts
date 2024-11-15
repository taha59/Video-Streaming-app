import { Component, OnInit, inject } from '@angular/core';
import { LoginResponse, OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  public api = "aa"
  title = 'youtube-clone-ui';
  private readonly oidcSecurityService: OidcSecurityService = inject(OidcSecurityService)
  constructor(){}

  ngOnInit(): void{
    this.oidcSecurityService
      .checkAuth()
      .subscribe(({isAuthenticated}) => {
        
        console.log("app is authenticated", isAuthenticated)
        
      });
  }

}
