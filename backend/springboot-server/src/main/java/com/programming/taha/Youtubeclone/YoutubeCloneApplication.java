package com.programming.taha.Youtubeclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableMongoAuditing
@SpringBootApplication
public class YoutubeCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeCloneApplication.class, args);
		System.out.println("Server started");
	}

}
