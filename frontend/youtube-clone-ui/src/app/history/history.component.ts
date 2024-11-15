import { Component, inject, OnInit } from '@angular/core';
import { VideoDto } from '../video-dto';
import { UserService } from '../user.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit{
  private readonly userService = inject(UserService)

  watchHistory: Array<VideoDto> = [];

  constructor(){
    this.userService.getUserHistory().subscribe((videos) => {
      this.watchHistory = videos
    })
  }
  ngOnInit(): void {
    
  }

}
