import { Component, OnInit, inject } from '@angular/core';
import { VideoService } from '../video.service';
import { VideoDto } from '../video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-featured',
  templateUrl: './featured.component.html',
  styleUrl: './featured.component.css'
})
export class FeaturedComponent implements OnInit {
  
  private readonly videoService = inject(VideoService)

  featuredVideos: Array<VideoDto> = [];


  constructor(){
    this.videoService.getAllVideos().subscribe(videos => {
      this.featuredVideos = videos
    })
  }
  
  
  ngOnInit(): void {
   
  }

}
