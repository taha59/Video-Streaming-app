package com.programming.taha.Youtubeclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Document(value = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String emailAddress;
    private String sub;
    private String pictureUrl;
    private Set<String> subscribedToUsers = ConcurrentHashMap.newKeySet();
    private Set<String> subscribers = ConcurrentHashMap.newKeySet();
    private Set<String> videoHistory = ConcurrentHashMap.newKeySet();
    private Set<String> likedVideos = ConcurrentHashMap.newKeySet();
    private Set<String> dislikedVideos = ConcurrentHashMap.newKeySet();

    public void addToLikedVideos(String videoId) {
        likedVideos.add(videoId);
    }

    public void addToDislikedVideos(String videoId) {
        dislikedVideos.add(videoId);
    }

    public void removeFromLikedVideos(String videoId) {
        likedVideos.remove(videoId);
    }

    public void removeFromDislikedVideos(String videoId) {
        dislikedVideos.remove(videoId);
    }

    public void addToVideoHistory(String videoId) {
        videoHistory.add(videoId);
    }

    public void addToSubscribedToUsers(String userId) { subscribedToUsers.add(userId);}

    public void addToSubscribers(String userId) { subscribers.add(userId);}

    public void removeFromSubscribedToUsers(String userId) { subscribedToUsers.remove(userId); }

    public void removeFromSubscribers(String userId) { subscribers.remove(userId); }

    //invoked whenever a video is deleted
    public void correctVideoHistory(String videoId){
        dislikedVideos.remove(videoId);
        videoHistory.remove(videoId);
        likedVideos.remove(videoId);
    }
}
