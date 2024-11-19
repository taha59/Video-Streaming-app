import { Component, inject, OnInit } from '@angular/core';
import { VideoService } from '../video.service';
import { YoutubeVideoDto } from '../youtube-video-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-video-search',
  templateUrl: './video-search.component.html',
  styleUrl: './video-search.component.css'
})
export class VideoSearchComponent implements OnInit{
  private readonly videoService = inject(VideoService)
  private readonly router = inject(Router)

  youtubeSearchResults: Array<YoutubeVideoDto> = [];
  searchQuery: string = '';

  constructor(){

  }

  searchVideos(){
    this.videoService.searchVideos(this.searchQuery).subscribe(data =>{
      this.youtubeSearchResults = data
    })
  }

  downloadYoutubeVideo(youtubeUrl: string){
    if (youtubeUrl !== ""){
      console.log("downloading video for URL:", youtubeUrl)
      this.videoService.downloadYoutubeVideo(youtubeUrl)
    }
  }

  ngOnInit(): void {}
}