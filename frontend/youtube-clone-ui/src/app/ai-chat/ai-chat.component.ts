import { Component, inject, Input, OnInit } from '@angular/core';
import { VideoService } from '../video.service';
import { FormControl, FormGroup } from '@angular/forms';
import { AiChatDto } from "../ai-chat-dto"

@Component({
  selector: 'app-ai-chat',
  templateUrl: './ai-chat.component.html',
  styleUrl: './ai-chat.component.css'
})
export class AiChatComponent implements OnInit{
  
  
  @Input()
  videoId!: string
  
  aiChatForm: FormGroup;

  aiChatHistory: Array<AiChatDto> = []
  

  private readonly videoService = inject(VideoService)

  constructor(){
    this.aiChatForm = new FormGroup({
      userPrompt: new FormControl('')
    })

  }

  ngOnInit(): void {
    this.getChatHistory()
  }

  getChatHistory(){
    this.videoService.getVideo(this.videoId).subscribe((data)=>{
      this.aiChatHistory = data.aiChatHistory
    })
  }
  startAiChat(){

    const userPrompt = this.aiChatForm.get('userPrompt')?.value

    this.videoService.startAiChat(this.videoId, userPrompt)
    .subscribe(()=>{
      this.getChatHistory()

      this.aiChatForm.get('userPrompt')?.reset()
    })


  }

  deleteAiChat(){
    this.videoService.deleteAiChat(this.videoId)
    .subscribe(()=>{
      this.getChatHistory()
    })
    
  }
}
