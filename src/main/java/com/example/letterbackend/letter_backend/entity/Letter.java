package com.example.letterbackend.letter_backend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "letters")
public class Letter {
    @Id
    private String id;
    private String title;  // 편지 제목
    private String content; // 편지 내용
    private String senderUsername; // 보낸 사람 (아이디)
    private String recipientUsername; // 받는 사람 (아이디)
    private LocalDateTime sentAt = LocalDateTime.now(); // 보낸 날짜
}
