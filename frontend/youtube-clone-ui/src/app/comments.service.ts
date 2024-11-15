import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentDto } from './comment-dto';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CommentsService {
  private readonly httpClient: HttpClient = inject(HttpClient)
  private readonly apiServer: string = environment.springboot_server_url
  constructor() { }

  postComment(commentDto: CommentDto, videoId: string): Observable<CommentDto>{
    return this.httpClient.post<CommentDto>(this.apiServer+"/api/videos/" + videoId + "/comment", commentDto)
  }

  getAllComments(videoId: string): Observable<Array<CommentDto>>{
    return this.httpClient.get<CommentDto[]>(this.apiServer+"/api/videos/" + videoId + "/comment")
  }
}
