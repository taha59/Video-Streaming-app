import { Component, Input, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../user.service';
import { CommentsService } from '../comments.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommentDto } from '../comment-dto';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.css'
})
export class CommentsComponent implements OnInit {
  private readonly userService = inject(UserService)
  private readonly commentService = inject(CommentsService)
  private readonly matSnackBar = inject(MatSnackBar)
  
  commentsForm: FormGroup;
  commentsDto: CommentDto[] = []

  @Input()
  videoId: string =''

  constructor(){
    this.commentsForm = new FormGroup({
      comment: new FormControl('')
    })

  }
  
  ngOnInit(): void {
    this.getComments()
  }

  getComments(){
    this.commentService.getAllComments(this.videoId).subscribe(data =>{
      this.commentsDto = data
    })
  }

  postComment(){
    const comment = this.commentsForm.get('comment')?.value

    this.userService.getUpdatedUser().subscribe((user) => {

      console.log("comment posted by ", user.id)
      const commentDto = {
        "commentText": comment,
        "authorName": user.fullName
      }
  
      this.commentService.postComment(commentDto, this.videoId).subscribe(() =>{
        this.matSnackBar.open("Comment posted!", "OK")
        
        this.commentsForm.get('comment')?.reset()
  
        this.getComments()
      })
    })
  }


}
