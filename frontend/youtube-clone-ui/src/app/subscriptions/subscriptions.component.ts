import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { VideoDto } from '../video-dto';

@Component({
  selector: 'app-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrl: './subscriptions.component.css'
})
export class SubscriptionsComponent implements OnInit{
  private readonly userService = inject(UserService)

  subscribedVideos: Array<VideoDto> = [];

  constructor(){
    this.userService.getSubscribedVideos().subscribe((videos) => {
      this.subscribedVideos = videos
    })
  }
  ngOnInit(): void {
    
  }

}