import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from '../video.service';
import { UserService } from '../user.service';
import { AiChatDto } from '../ai-chat-dto'

@Component({
  selector: 'app-video-detail',
  templateUrl: './video-detail.component.html',
  styleUrl: './video-detail.component.css'
})
export class VideoDetailComponent implements OnInit {

  private readonly activatedRoute = inject(ActivatedRoute)
  private readonly videoService = inject(VideoService)
  private readonly userService = inject(UserService)

  videoId: string;
  videoUrl: string;
  videoTitle: string
  videoDescription: string
  videoTags: Array<string> = []
  likeCount: number = 0
  dislikeCount: number = 0
  viewCount: number = 0
  userId: string = ""
  userName: string = ""
  subscriberCount: number = 0
  aiOverview: string = ""
  createdDate: string
  aiChatHistory: Array<AiChatDto> = []

  isSubscribed: boolean = false

  constructor(){
    this.videoId = this.activatedRoute.snapshot.params['videoId']

    this.videoService.getVideo(this.videoId).subscribe(data =>
      {

        if (!data.title){
          data.title = "[No Title]"
        }

        this.videoUrl = data.videoUrl
        this.videoTitle = data.title
        this.videoDescription = data.description
        this.videoTags = data.tags
        this.likeCount = data.likeCount
        this.dislikeCount = data.dislikeCount
        this.viewCount = data.viewCount
        this.userId = data.userId
        this.createdDate = data.createdDate
        this.aiChatHistory = data.aiChatHistory
        
        // update the subscribe status for the current user
        this.userService.getUpdatedUser().subscribe(currUser =>{
          this.subscriberCount = currUser.subscribedToUsers.length
          this.isSubscribed = currUser.subscribedToUsers.includes(data.userId)

          this.userService.getTargetUser(data.userId).subscribe(targetUser => {
            this.userName = targetUser.fullName
          })

        })
      }
    )
  }

  ngOnInit(): void{
  }

  likeVideo(){
    this.videoService.likeVideo(this.videoId).subscribe((data)=>{
      this.likeCount = data.likeCount
      this.dislikeCount = data.dislikeCount
    })
  }

  dislikeVideo(){
    this.videoService.dislikeVideo(this.videoId).subscribe((data)=>{
      this.likeCount = data.likeCount
      this.dislikeCount = data.dislikeCount
    })
  }

  subscribeToUser(){
    console.log("subscribing to: ", this.userId)

    this.userService.subscribeToUser(this.userId).subscribe(data => {
      this.isSubscribed = true
    })


  }

  unsubscribeToUser(){
    console.log("unsubscribing to: ", this.userId)

    this.userService.unsubscribeToUser(this.userId).subscribe(data => {
      this.isSubscribed = false
    })
  }

}
