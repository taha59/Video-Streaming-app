import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { VideoDto } from '../video-dto';

@Component({
  selector: 'app-liked-video',
  templateUrl: './liked-video.component.html',
  styleUrl: './liked-video.component.css'
})
export class LikedVideoComponent implements OnInit{
  private readonly userService = inject(UserService)

  likedVideos: Array<VideoDto> = [];

  constructor(){
    this.userService.getLikedVideos().subscribe((videos) => {
      this.likedVideos = videos
    })
  }
  ngOnInit(): void {
    
  }

}