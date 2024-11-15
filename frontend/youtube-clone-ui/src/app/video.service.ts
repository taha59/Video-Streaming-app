import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { UploadVideoResponse } from './upload-video/UploadVideoResponse';
import { VideoDto } from './video-dto';
import { YoutubeVideoDto } from './youtube-video-dto';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class VideoService {
  private readonly httpClient = inject(HttpClient);
  private readonly apiServer = environment.springboot_server_url;

  constructor() { }

  uploadVideoByFileDrop(fileEntry: File): Observable<UploadVideoResponse> {
    const formData = new FormData();
    formData.append('file', fileEntry, fileEntry.name);

    console.log('Uploading video...');

    return this.httpClient.post<UploadVideoResponse>(this.apiServer + "/api/videos", formData)
  }

  uploadVideoByYoutubeUrl(youtubeUrl: string): Observable<UploadVideoResponse> {
    const formData = new FormData();
    formData.append('youtubeUrl', youtubeUrl);

    console.log('Uploading video via url...');

    return this.httpClient.post<UploadVideoResponse>(this.apiServer + "/api/videos/upload-by-url", formData)
  }

  uploadThumbnail(fileEntry: File, videoId: string): Observable<string> {
    const formData = new FormData();
    formData.append('file', fileEntry, fileEntry.name);
    formData.append('videoId', videoId);

    console.log('Uploading thumbnail...');

    return this.httpClient.post(this.apiServer + "/api/videos/thumbnail", formData, { responseType: 'text' })
  }

  getVideo(videoId: string): Observable<VideoDto> {
    return this.httpClient.get<VideoDto>(this.apiServer + "/api/videos/" + videoId)
  }

  editVideoMetadata(videoDto: VideoDto): Observable<any> {
    return this.httpClient.put<VideoDto>(this.apiServer + "/api/videos", videoDto)
  }

  getAllVideos(): Observable<Array<VideoDto>> {
    return this.httpClient.get<Array<VideoDto>>(this.apiServer + "/api/videos")
  }

  likeVideo(videoId: string): Observable<VideoDto> {
    return this.httpClient.post<VideoDto>(this.apiServer + "/api/videos/" + videoId + "/like", null)
  }

  dislikeVideo(videoId: string): Observable<VideoDto> {
    return this.httpClient.post<VideoDto>(this.apiServer + "/api/videos/" + videoId + "/dislike", null)
  }

  searchVideos(searchQuery: string): Observable<Array<YoutubeVideoDto>>{
    const formData = new FormData();

    console.log(searchQuery)
    formData.append('searchQuery', searchQuery)

    return this.httpClient.post<Array<YoutubeVideoDto>>(`${environment.flask_server_url}/search`, formData)
  }

  deleteVideoById(videoId: string){
    return this.httpClient.delete(this.apiServer+"/api/videos/"+videoId, {responseType: 'text'})
  }

  downloadVideo(youtubeUrl: string){

    const formData = new FormData();
    formData.append('youtubeUrl', youtubeUrl);
    // Set responseType to 'blob' and observe 'response' to receive both headers and binary data
    this.httpClient.post(environment.flask_server_url +"/download-video", formData, { responseType: 'blob', observe: 'response' })
  .subscribe((response) => {
    const blob = response.body; // Blob object containing the video file
    // Check if the body is not null
    if (blob) {
      let videoTitle = 'video.mp4'; // Default title if not provided
      
      //get video title from the header
      const customTitle = response.headers.get('X-Video-Title');
      
      if (customTitle) {
        const bytes = atob(customTitle)
        // Decode Base64 to original UTF-8 string
        videoTitle = new TextDecoder('utf-8').decode(new Uint8Array([...bytes].map(char => char.charCodeAt(0))));
      }
      

      // Create a URL from the Blob and trigger download
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = videoTitle + ".mp4"; // Set the file name for download
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url); // Clean up after download
    }
  });


  }

}
