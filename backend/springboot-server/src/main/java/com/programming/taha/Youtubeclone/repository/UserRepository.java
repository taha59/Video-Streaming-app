package com.programming.taha.Youtubeclone.repository;

import com.programming.taha.Youtubeclone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>{

    Optional<User> findBySub(String sub);
}
