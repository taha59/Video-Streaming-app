import { HttpClient } from '@angular/common/http';
import { Injectable, inject, numberAttribute } from '@angular/core';
import { Observable } from 'rxjs';
import { UserDto } from './user-dto';
import { VideoDto } from './video-dto';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiServer = environment.springboot_server_url
  private readonly httpClient: HttpClient = inject(HttpClient)
  private user: UserDto
  constructor() { }

  registerUser() {
    this.httpClient.get<UserDto>(this.apiServer+"/api/user/register")
    .subscribe(user =>{
      this.user = user
    })
  }

  getUpdatedUser(): Observable<UserDto> {
    return this.httpClient.get<UserDto>(this.apiServer+"/api/user/register")
  }

  getTargetUser(userId: string): Observable<UserDto> {
    return this.httpClient.get<UserDto>(this.apiServer+"/api/user/" + userId)
  }

  subscribeToUser(userId: string): Observable<boolean>{
    return this.httpClient.post<boolean>(this.apiServer+"/api/user/subscribe/"+userId, null)
  }

  unsubscribeToUser(userId: string): Observable<boolean>{
    return this.httpClient.post<boolean>(this.apiServer+"/api/user/unsubscribe/"+userId, null)
  }

  getUserHistory(): Observable<Array<VideoDto>>{
    return this.httpClient.get<Array<VideoDto>>(this.apiServer+"/api/user/history")
  }

  getLikedVideos(): Observable<Array<VideoDto>>{
    return this.httpClient.get<Array<VideoDto>>(this.apiServer+"/api/user/liked-videos")
  }

  getSubscribedVideos(): Observable<Array<VideoDto>>{
    return this.httpClient.get<Array<VideoDto>>(this.apiServer+"/api/user/subscriptions")
  }
}
