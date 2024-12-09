import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';
import { environment } from 'src/environments/environment.development';

@NgModule({
    imports: [AuthModule.forRoot({
        config: {
            authority: environment.auth_authority,
            redirectUrl: window.location.origin + '/callback',
            clientId: environment.auth_client_id,
            scope: 'openid profile offline_access email',
            responseType: 'code',
            silentRenew: true,
            useRefreshToken: true,
            postLogoutRedirectUri: window.location.origin,
            secureRoutes: [environment.flask_server_url, environment.springboot_server_url],
        
            customParamsAuthRequest: {
              audience: environment.logical_api_url,
            },
            
        }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}
