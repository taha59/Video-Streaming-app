package com.programming.taha.Youtubeclone.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.taha.Youtubeclone.dto.UserInfoDto;
import com.programming.taha.Youtubeclone.model.User;
import com.programming.taha.Youtubeclone.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    @Value("${auth0.userInfoEndpoint}")
    private String userInfoEndpoint;

    private final UserRepository userRepository;

    public User registerUser(Jwt principal){

        //if the user sub is already registered
        Optional<User> userBySubject = userRepository.findBySub(principal.getSubject());
        if (userBySubject.isPresent()){
            return userBySubject.get();
        }

        //retrieve user info from userInfo endpoint
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userInfoEndpoint))
                .setHeader("Authorization", String.format("Bearer %s", principal.getTokenValue()))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            ObjectMapper objectMapper = new ObjectMapper();

            //configure so that it doesn't fail when the body has diff fields than userinfo dto
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UserInfoDto userInfoDto = objectMapper.readValue(body, UserInfoDto.class);

            //set user details before pushing in database
            User user = new User();
            user.setFirstName(userInfoDto.getName());
            user.setLastName(userInfoDto.getFamilyName());
            user.setFullName(userInfoDto.getName());
            user.setEmailAddress(userInfoDto.getEmail());
            user.setSub(userInfoDto.getSub());
            user.setPictureUrl(userInfoDto.getPicture());

            return userRepository.save(user);
        }
        catch(Exception exception){
            throw new RuntimeException("Exception occurred while registering user", exception);
        }

    }
}
