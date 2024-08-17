package com.programming.taha.Youtubeclone.controller;

import com.programming.taha.Youtubeclone.dto.CommentDto;
import com.programming.taha.Youtubeclone.dto.UploadVideoResponse;
import com.programming.taha.Youtubeclone.dto.VideoDto;
import com.programming.taha.Youtubeclone.dto.YoutubeMetaDataDto;
import com.programming.taha.Youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    //endpoint for uploading a video
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId){
        return videoService.uploadVideo(file, "", userId);
    }

    //endpoint for uploading thumbnail
    @PostMapping("/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId){
        return videoService.uploadThumbnail(file, videoId);
    }

    //endpoint for editing video
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VideoDto editVideoMetadata(@RequestBody VideoDto videoDto){
        return videoService.editVideo(videoDto);
    }

    //endpoint for getting metadata of a particular video
    @GetMapping("/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto getVideo(@PathVariable String videoId) {
        return videoService.getVideoDetails(videoId);
    }

    //endpoint for liking a video
    @PostMapping("/{videoId}/like")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto likeVideo(@PathVariable String videoId){
        return videoService.likeVideo(videoId);
    }

    //endpoint for disliking a video
    @PostMapping("/{videoId}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto dislikeVideo(@PathVariable String videoId){
        return videoService.dislikeVideo(videoId);
    }

    @PostMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public void addComment(@PathVariable String videoId, @RequestBody CommentDto commentDto){

        videoService.addComment(videoId, commentDto);
    }

    @GetMapping("{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable String videoId){
        return videoService.getAllComments(videoId);
    }

    @PostMapping("{videoId}/delete-comments")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllComments(@PathVariable String videoId){
        videoService.deleteAllComments(videoId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getAllVideos(){
        return videoService.getAllVideos();
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public String deleteAllVideos(){
        videoService.deleteAllVideos();
        return "Videos deleted successfully";
    }

    @PostMapping("upload-by-url")
    @ResponseStatus(HttpStatus.OK)
    public UploadVideoResponse uploadByYoutubeUrl(@RequestParam String youtubeUrl, @RequestParam String userId) {
        return videoService.uploadByYoutubeUrl(youtubeUrl, userId);
    }

    @PostMapping("youtube-search")
    @ResponseStatus(HttpStatus.OK)
    public List<YoutubeMetaDataDto> youtubeVideoSearch(@RequestParam String searchQuery) {
        return videoService.youtubeVideoSearch(searchQuery);
    }


}