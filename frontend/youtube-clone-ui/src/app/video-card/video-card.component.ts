import { Component, inject, Input, OnInit } from '@angular/core';
import { VideoDto } from '../video-dto';
import { VideoService } from '../video.service';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css'
})
export class VideoCardComponent implements OnInit {
  
  @Input()
  video!: VideoDto

  videoService = inject(VideoService)
  constructor(){}
  
  ngOnInit(): void {
      
  }

  deleteVideo(videoId: string){
    this.videoService.deleteVideoById(videoId).subscribe(data => {
      console.log(videoId + "deleted!")
      window.location.reload();
    })
  }
}
