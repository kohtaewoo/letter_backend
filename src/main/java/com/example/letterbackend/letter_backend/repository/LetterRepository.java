package com.example.letterbackend.letter_backend.repository;

import com.example.letterbackend.letter_backend.entity.Letter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LetterRepository extends MongoRepository<Letter, String> {
    List<Letter> findByRecipientUsername(String recipientUsername); // 받은 편지 조회
}
