package com.example.letterbackend.letter_backend.repository;

import com.example.letterbackend.letter_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
