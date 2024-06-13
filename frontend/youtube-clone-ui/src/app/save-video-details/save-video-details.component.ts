import { Component, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatChipEditedEvent, MatChipInputEvent } from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from '../video.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { VideoDto } from '../video-dto';

@Component({
  selector: 'app-save-video-details',
  templateUrl: './save-video-details.component.html',
  styleUrls: ['./save-video-details.component.css']
})
export class SaveVideoDetailsComponent implements OnInit{

  saveVideoDetailsForm: FormGroup;
  title:FormControl = new FormControl('');
  videoStatus:FormControl = new FormControl('PUBLIC');
  description:FormControl = new FormControl('');

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  tags: string[] = [];
  droppedFile: File
  selectedFileName = ''
  videoId = ''
  fileSelected : boolean = false
  videoUrl: string
  thumbnailUrl : string

  announcer = inject(LiveAnnouncer);

  constructor(private activatedRoute: ActivatedRoute, private videoService: VideoService
    ,private matSnackBar: MatSnackBar
  ){
    this.videoId = this.activatedRoute.snapshot.params['videoId']
    this.videoService.getVideo(this.videoId).subscribe(data =>
      {
        this.videoUrl = data.videoUrl
        this.thumbnailUrl = data.thumbnailUrl
      }
    )

    this.saveVideoDetailsForm = new FormGroup(
      {
        title: this.title,
        description: this.description,
        videoStatus: this.videoStatus,
      }
    )
  
  } 
  ngOnInit(): void {}

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our string
    if (value) {
      this.tags.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(tag : string): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);

      this.announcer.announce(`Removed ${tag}`);
    }
  }

  edit(tag: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    // Remove fruit if it no longer has a name
    if (!value) {
      this.remove(tag);
      return;
    }

    // Edit existing fruit
    const index = this.tags.indexOf(tag);
    if (index >= 0) {
      this.tags[index] = value;
    }
  }

  onFileSelected($event: Event){
    //@ts-ignorets-ignore
    this.droppedFile = $event.target.files[0]
    this.selectedFileName = this.droppedFile.name
    this.fileSelected = true
  }

  onUpload(){
    this.videoService.uploadThumbnail(this.droppedFile, this.videoId)
    .subscribe(data => {
      console.log(data)
      this.matSnackBar.open("Thumbnail Uploaded!", "OK")
    })
  }

  editVideoMetaData(){

    const videoDto: VideoDto = {
      id: this.videoId,
      title: this.title.value,
      description: this.description.value,
      tags: this.tags,
      videoUrl: this.videoUrl,
      videoStatus: this.videoStatus.value,
      thumbnailUrl: this.thumbnailUrl
    };

    //https call to backend edit video metadata. takes videodto as input and reponse is videoDto
    this.videoService.editVideoMetadata(videoDto).subscribe(data => {
      console.log(data)
      this.matSnackBar.open("Video Details edited Successfully!!", "OK")
    })
  }

}
