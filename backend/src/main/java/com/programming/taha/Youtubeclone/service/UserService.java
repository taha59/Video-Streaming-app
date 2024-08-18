package com.programming.taha.Youtubeclone.service;

import com.programming.taha.Youtubeclone.dto.VideoDto;
import com.programming.taha.Youtubeclone.model.User;
import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.repository.UserRepository;
import com.programming.taha.Youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    //Helper methods for user service
    public User getUserById(String userId){
        return userRepository.findById(userId).
                orElseThrow(() ->
                        new IllegalArgumentException
                                ("Cannot find user with id: " + userId));
    }

    public User getCurrentUser(){

        String sub = ((Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getSubject();

        return userRepository.findBySub(sub)
                .orElseThrow(()->
                        new IllegalArgumentException("Cannot find user with sub - " + sub));
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addToDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDislikedVideos(videoId);
        userRepository.save(currentUser);
    }


    public boolean ifLikedVideo(String videoId){
        return getCurrentUser().
                getLikedVideos().stream().
                anyMatch(likedVideo -> likedVideo.equals(videoId));

    }

    public boolean ifDislikedVideo(String videoId){
        return getCurrentUser().
                getDislikedVideos().stream().
                anyMatch(dislikedVideo -> dislikedVideo.equals(videoId));

    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void removeFromDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addToVideoHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        //if the user is already subscribed to target userid then return
        User currentUser = getCurrentUser();

        //add the user being subscribed to addToSubscribers set
        currentUser.addToSubscribedToUsers(userId);
        //save the changes of current user and the user that is being subscribed to
        userRepository.save(currentUser);

        //get user by id
        User user = getUserById(userId);

        //add the current user to the target user's subscribers
        user.addToSubscribers(currentUser.getId());

        userRepository.save(user);

    }

    public void unsubscribeUser(String userId) {
        User currentUser = getCurrentUser();

        //if a current user unsubscribes to another user
        currentUser.removeFromSubscribedToUsers(userId);

        userRepository.save(currentUser);

        //get User by id
        User user = getUserById(userId);

        //since the current user is no longer subscribed to target user
        //, remove the current user from the targets user's subscriber list
        user.removeFromSubscribers(currentUser.getId());

        //save the changes of current user and the user that is being subscribed to
        userRepository.save(user);
    }

    public List<VideoDto> getUserHistory() {
        User user = getCurrentUser();

        // Fetch the user's video history set
        Set<String> watchHistory = user.getVideoHistory();

        // Initialize a list to store the video DTOs
        List<VideoDto> videoDtoList = new ArrayList<>();

        // Iterate through the video history set
        for (String videoId : watchHistory) {
            // Find the video by its ID
            Optional<Video> videoOpt = videoRepository.findById(videoId);
            //if video exists convert it to videoDto and add it to list
            videoOpt.ifPresent(video -> videoDtoList.add(setToVideoDto(video)));
        }

        // Return the list of video DTOs
        return videoDtoList;
    }

    public List<VideoDto> getLikedVideos() {
        User user = getCurrentUser();

        // Fetch the user's liked videos set
        Set<String> likedVideos = user.getLikedVideos();

        // Initialize a list to store the video DTOs
        List<VideoDto> videoDtoList = new ArrayList<>();

        // Iterate through the video history set
        for (String videoId : likedVideos) {
            // Find the video by its ID
            Optional<Video> videoOpt = videoRepository.findById(videoId);
            //if video exists convert it to videoDto and add it to list
            videoOpt.ifPresent(video -> videoDtoList.add(setToVideoDto(video)));
        }

        // Return the list of video DTOs
        return videoDtoList;
    }

    public List<VideoDto> getSubscribedVideos() {
        User user = getCurrentUser();

        // Fetch the users that are subscribed to current user
        Set<String> subscribedToSet = user.getSubscribedToUsers();

        // Initialize a set to store the video DTOs
        Set<VideoDto> videoDtoSet = new HashSet<>();

        // Iterate through the video history set
        for (String subscribedTo: subscribedToSet) {
            // Find the video by its ID
            Optional<List<Video>> videoOpt = videoRepository.findByUserId(subscribedTo);

            videoOpt.ifPresent(videos -> {
                videos.forEach(video -> videoDtoSet.add(setToVideoDto(video)));
            });
        }

        // Return the list of video DTOs
        return videoDtoSet.stream().toList();
    }

    //helper methods
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
}
