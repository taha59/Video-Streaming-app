import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { VideoService } from '../video.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})


export class HeaderComponent implements OnInit{

  private readonly videoService = inject(VideoService)
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
    this.oidcSecurityService.authorize()
  }

  logoff(){

    this.oidcSecurityService.logoffAndRevokeTokens()
    .subscribe(() => {
      this.oidcSecurityService.logoffLocal();
    });

  }
}
