package com.programming.taha.Youtubeclone.controller;


import com.programming.taha.Youtubeclone.dto.VideoDto;
import com.programming.taha.Youtubeclone.model.User;
import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.service.UserRegistrationService;
import com.programming.taha.Youtubeclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationService userRegistrationService;
    private final UserService userService;

    //endpoint for registering a user
    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public User register(@AuthenticationPrincipal Jwt jwt){

        return userRegistrationService.registerUser(jwt);
    }

    //endpoint for subscribing to a user
    @PostMapping("/subscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean subscribeUser(@PathVariable String userId){
        userService.subscribeUser(userId);
        return true;
    }

    //endpoint for unsubscribing to a user
    @PostMapping("/unsubscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean unsubscribeUser(@PathVariable String userId){
        userService.unsubscribeUser(userId);
        return false;
    }

    //get the video history of a user
    @GetMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getUserHistory(){
        return userService.getUserHistory();
    }

    @GetMapping("/liked-videos")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getLikedVideos(){
        return userService.getLikedVideos();
    }

    @GetMapping("/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getSubscribedVideos(){
        return userService.getSubscribedVideos();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable String userId){
        return userService.getUserById(userId);
    }

}
