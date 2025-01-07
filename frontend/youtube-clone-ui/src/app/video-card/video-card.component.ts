import { Component, inject, Input, OnInit } from '@angular/core';
import { VideoDto } from '../video-dto';
import { VideoService } from '../video.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css'
})
export class VideoCardComponent implements OnInit {
  
  @Input()
  video!: VideoDto

  userName: string = ""
  pictureUrl : string = ""
  videoService = inject(VideoService)
  userService = inject(UserService)

  constructor(){
  }
  
  ngOnInit(): void {
    // if video exists

  
    if(this.video){

      if (!this.video.title){
        this.video.title = "[No Title]"
      }
      this.userService.getTargetUser(this.video.userId).subscribe(data =>{
      this.userName = data.fullName
      this.pictureUrl = data.pictureUrl
      })
    }
  }

  deleteVideo(videoId: string){
    this.videoService.deleteVideoById(videoId).subscribe(() => {
      console.log(videoId + "deleted!")
      window.location.reload();
    })
  }

  downloadUserVideo(){
    this.videoService.downloadUserVideo(this.video.videoUrl, this.video.title)
  }
}
