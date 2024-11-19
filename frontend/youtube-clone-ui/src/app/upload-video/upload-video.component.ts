import { Component, inject } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { VideoService } from '../video.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload-video',
  templateUrl: './upload-video.component.html',
  styleUrls: ['./upload-video.component.css']
})
export class UploadVideoComponent {

  private readonly videoService = inject(VideoService)
  private readonly router = inject(Router)

  public files: NgxFileDropEntry[] = [];
  fileUploaded = false
  fileEntry: FileSystemFileEntry | undefined
  youtubeUrl: string = ''


  constructor(){}

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        this.fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        this.fileEntry.file((file: File) => {
          
          // Here you can access the real file
          console.log(droppedFile.relativePath, file);
          this.fileUploaded = true;

        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  public fileOver(event : any){
    console.log(event);
  }

  public fileLeave(event : any){
    console.log(event);
  }

  uploadVideoByFileDrop(){
    if (this.fileEntry !== undefined){

      this.fileEntry.file(file => {

        this.videoService.uploadVideoByFileDrop(file).subscribe( data => {

          this.router.navigateByUrl("/save-video-details/" + data.videoId);
          
        })
      })
      
    }
  }

  uploadVideoByYoutubeUrl(){
    if (this.youtubeUrl !== ""){
      this.videoService.downloadYoutubeVideo(this.youtubeUrl)
    }
  }
}

