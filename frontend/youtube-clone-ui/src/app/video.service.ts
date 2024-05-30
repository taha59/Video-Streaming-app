import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UploadVideoResponse } from './upload-video/UploadVideoResponse';

@Injectable({
  providedIn: 'root'
})
export class VideoService {

  constructor(private httpClient: HttpClient) { }

  uploadVideo(fileEntry: File): Observable<UploadVideoResponse> {
    const formData = new FormData();
    formData.append('file', fileEntry, fileEntry.name);

    console.log('Uploading video...');

    // Using HTTP for local development
    return this.httpClient.post<UploadVideoResponse>("http://localhost:8080/api/videos", formData);
  }
}
