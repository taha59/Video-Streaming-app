package com.programming.taha.Youtubeclone.repository;

import com.programming.taha.Youtubeclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends MongoRepository<Video, String> {
    Optional<List<Video>> findByUserId(String userId);
}
