package com.programming.taha.Youtubeclone.service;

import com.programming.taha.Youtubeclone.dto.CommentDto;
import com.programming.taha.Youtubeclone.dto.UploadVideoResponse;
import com.programming.taha.Youtubeclone.dto.VideoDto;
import com.programming.taha.Youtubeclone.model.Comment;
import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final UserService userService;
    private final VideoRepository videoRepository;
    private final TranscriptionService transcriptionService;
    private final LlamaAiService llamaAiService;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile) {
        // Upload file to AWS S3 and get the video URL
        String videoURL = s3Service.uploadFile(multipartFile);

        var video = new Video();
        video.setVideoUrl(videoURL);
        video.setUserId(userService.getCurrentUser().getId());

        var savedVideo = videoRepository.save(video);

        // Return the saved video response
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }


    public VideoDto editVideo(VideoDto videoDto) {

        Video savedVideo = getVideoById(videoDto.getId());
        // change the fields of the saved video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        var savedVideo = getVideoById(videoId);

        var thumbnailUrl = s3Service.uploadFile(file);

        savedVideo.setThumbnailUrl(thumbnailUrl);
        videoRepository.save(savedVideo);

        return thumbnailUrl;
    }

    Video getVideoById(String videoId){
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException( "Video id Not Found!!" + videoId ));
    }

    //function gets called whenever user requests for video details
    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        //increase view count anytime video details are requested
        increaseViewCount(savedVideo);
        userService.addToVideoHistory(videoId);

        return setToVideoDto(savedVideo);
    }

    private void increaseViewCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    /*method for when the like button is pressed*/
    public VideoDto likeVideo(String videoId) {
        Video video = getVideoById(videoId);

        //if a user already liked the video
        if (userService.ifLikedVideo(videoId)){
            video.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }
        //if the user already disliked the video
        else if(userService.ifDislikedVideo(videoId)){
            video.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
            video.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        else{
            video.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(video);

        return setToVideoDto(video);
    }

    //when the dislike button is pressed
    public VideoDto dislikeVideo(String videoId) {
        Video video = getVideoById(videoId);

        //if a user already liked the video
        if (userService.ifLikedVideo(videoId)){
            video.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            video.incrementDisLikes();
            userService.addToDislikedVideos(videoId);
        }
        //if the user already disliked the video
        else if(userService.ifDislikedVideo(videoId)){
            video.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
        }
        else{
            video.incrementDisLikes();
            userService.addToDislikedVideos(videoId);
        }


        videoRepository.save(video);

        return setToVideoDto(video);
    }

    public void addComment(String videoId, CommentDto commentDto){
        Video video = getVideoById(videoId);
        Comment comment = new Comment();

        comment.setText(commentDto.getCommentText());
        comment.setAuthorName(commentDto.getAuthorName());

        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);

        List<Comment> commentList = video.getCommentList();
        return commentList.stream().map(this::setToCommentDto)
                .toList();
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::setToVideoDto).toList();
    }

    public void deleteAllComments(String videoId){
        Video video = getVideoById(videoId);
        video.deleteAllComments();
        videoRepository.save(video);
    }

    //sets up videoDto
    private VideoDto setToVideoDto(Video video){
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(video.getVideoUrl());
        videoDto.setThumbnailUrl(video.getThumbnailUrl());
        videoDto.setId(video.getId());
        videoDto.setTitle(video.getTitle());
        videoDto.setDescription(video.getDescription());
        videoDto.setTags(video.getTags());
        videoDto.setVideoStatus(video.getVideoStatus());
        videoDto.setLikeCount(video.getLikes().get());
        videoDto.setDislikeCount(video.getDislikes().get());
        videoDto.setViewCount(video.getViewCount().get());
        videoDto.setUserId(video.getUserId());
        videoDto.setCreatedDate(getFormattedDate(video));
        videoDto.setAiChatHistory(video.getAiChatHistory());

        return videoDto;
    }

    //sets all comment dto fields
    private CommentDto setToCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();

        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorName(comment.getAuthorName());

        return commentDto;
    }

    private String getFormattedDate(Video video){

        Instant createdDate = video.getCreatedDate();
        LocalDateTime dateTime = LocalDateTime.ofInstant(createdDate, ZoneId.systemDefault());

        // Define the desired format (e.g., "dd MMMM yyyy" or "MM/dd/yyyy")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy"); // For "17 March 2023"

        return dateTime.format(formatter);
    }

    public void deleteVideoById(String videoId) {

        Video video = getVideoById(videoId);
        s3Service.deleteFile(video.getVideoUrl());

        videoRepository.deleteById(videoId);
    }

    public ResponseEntity<Resource> downloadUserVideo(String s3Url) {
        return s3Service.downloadFile(s3Url);
    }

    public String AIChat(String videoId, String userPrompt){
        Video video = getVideoById(videoId);

        if (video.getTranscription() == null){
            String transcript = transcriptionService.getTranscriptionFromMediaFile(video.getVideoUrl());
            video.setTranscription(transcript);
        }

        String aiResponse = llamaAiService.startChat(userPrompt, video);
        videoRepository.save(video);

        return aiResponse;
    }

    public void deleteAiChatHistory(String videoId){
        Video video = getVideoById(videoId);

        llamaAiService.deleteAiChatHistory(video);

        videoRepository.save(video);
    }

}