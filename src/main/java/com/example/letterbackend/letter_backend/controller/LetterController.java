package com.example.letterbackend.letter_backend.controller;

import com.example.letterbackend.letter_backend.entity.Letter;
import com.example.letterbackend.letter_backend.entity.User;
import com.example.letterbackend.letter_backend.repository.LetterRepository;
import com.example.letterbackend.letter_backend.repository.UserRepository;
import com.example.letterbackend.letter_backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/letters")
@CrossOrigin(origins = "http://localhost:3000") // ✅ CORS 설정
public class LetterController {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LetterController(LetterRepository letterRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.letterRepository = letterRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ✅ 편지 보내기 (POST /api/letters/send)
    @PostMapping("/send")
    public ResponseEntity<?> sendLetter(@RequestHeader("Authorization") String token, @RequestBody Letter letter) {
        String senderUsername = extractUsernameFromToken(token);
        if (senderUsername == null) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        letter.setSenderUsername(senderUsername);
        letterRepository.save(letter);
        return ResponseEntity.ok("편지가 성공적으로 보내졌습니다.");
    }

    // ✅ 받은 편지 목록 조회 (GET /api/letters/received) - 편지 내용도 포함
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedLetters(@RequestHeader("Authorization") String token) {
        String recipientUsername = extractUsernameFromToken(token);
        if (recipientUsername == null) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        List<Letter> letters = letterRepository.findByRecipientUsername(recipientUsername);

        // ✅ senderUsername을 senderName으로 변환하여 응답 반환 (편지 내용 포함)
        List<Object> response = letters.stream().map(letter -> {
            Optional<User> sender = userRepository.findByUsername(letter.getSenderUsername());
            return new Object() {
                public final String id = letter.getId();
                public final String title = letter.getTitle();
                public final String content = letter.getContent(); // 🔹 편지 내용 추가
                public final String senderName = sender.map(User::getName).orElse("알 수 없음");
                public final String recipientUsername = letter.getRecipientUsername();
                public final String sentAt = letter.getSentAt().toString();
            };
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ✅ JWT에서 사용자 이름 추출하는 메서드 (중복 제거)
    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return jwtUtil.validateToken(token.replace("Bearer ", ""));
    }
}
