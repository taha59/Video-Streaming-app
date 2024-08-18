package com.programming.taha.Youtubeclone.service;

import com.programming.taha.Youtubeclone.dto.CommentDto;
import com.programming.taha.Youtubeclone.dto.UploadVideoResponse;
import com.programming.taha.Youtubeclone.dto.VideoDto;
import com.programming.taha.Youtubeclone.dto.YoutubeMetaDataDto;
import com.programming.taha.Youtubeclone.model.Comment;
import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final UserService userService;
    private final VideoRepository videoRepository;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile, YoutubeMetaDataDto youtubeMetaDataDto) {
        //upload file to AWS

        String videoURL = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoURL);
        video.setThumbnailUrl(youtubeMetaDataDto.getThumbnailUrl());
        video.setTitle(youtubeMetaDataDto.getTitle());
        video.setUserId(userService.getCurrentUser().getId());

        var saved_video = videoRepository.save(video);
        return new UploadVideoResponse(saved_video.getId(), saved_video.getVideoUrl());
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

        //increase view count everytime anytime video details are requested
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
        comment.setAuthorId(commentDto.getAuthorId());

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

        return videoDto;
    }

    //sets all comment dto fields
    private CommentDto setToCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();

        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());

        return commentDto;
    }

    public UploadVideoResponse uploadByYoutubeUrl(String youtubeUrl) {

        String filePath;
        String thumbnailUrl;
        String title;

        //execute python code for downloading YouTube video by its url
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "downloadYoutubeVideo.py", youtubeUrl);
        processBuilder.redirectErrorStream(true); // Merge stderr with stdout


        Process process = null;
        try {
            process = processBuilder.start();

            // Read and print the output from the Python script (stdout)
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            filePath = reader.readLine();
            thumbnailUrl = reader.readLine();
            title = reader.readLine();

            System.out.println("file path: "+filePath);

            // Wait for the Python process to complete
            int exitCode = process.waitFor();
            if(exitCode == 0) System.out.println("python script executed successfully!");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        //convert the file stored in tmp dir to a multipart file before uploading it to S3
        Path path = Paths.get(filePath);
        try {
            byte[] data = Files.readAllBytes(path);
            System.out.println(filePath);
            System.out.println(thumbnailUrl);

            MultipartFile multipartFile = new MockMultipartFile(
                    filePath,
                    filePath,
                    "video/mp4",
                    data);

            return uploadVideo(multipartFile, new YoutubeMetaDataDto(youtubeUrl, thumbnailUrl, title));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public List<YoutubeMetaDataDto> youtubeVideoSearch(String searchQuery) {
        List<YoutubeMetaDataDto> youtubeVideos = new ArrayList<>();

        String watchUrl = "";
        String thumbnailUrl = "";
        String title = "";

        //execute python code for downloading YouTube video by its url
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "searchYoutubeVideo.py", searchQuery);
        processBuilder.redirectErrorStream(true); // Merge stderr with stdout


        Process process = null;
        try {
            process = processBuilder.start();

            // Read and print the output from the Python script (stdout)
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((title = reader.readLine()) != null &&
                     (watchUrl = reader.readLine()) != null &&
                     (thumbnailUrl = reader.readLine()) != null
             ) {

                 youtubeVideos.add(new YoutubeMetaDataDto(watchUrl, thumbnailUrl, title));
             }

            // Wait for the Python process to complete

            int exitCode = process.waitFor();
            if(exitCode == 0) System.out.println("python video search executed successfully!");
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (process != null) {

                process.destroy();
            }
        }

        return youtubeVideos;
    }

    public void deleteVideoById(String videoId) {

        Optional<Video> video = videoRepository.findById(videoId);
        video.ifPresent(value -> s3Service.deleteFile(value.getVideoUrl()));

        videoRepository.deleteById(videoId);
    }
}
