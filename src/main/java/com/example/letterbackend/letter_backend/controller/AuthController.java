package com.example.letterbackend.letter_backend.controller;

import com.example.letterbackend.letter_backend.entity.User;
import com.example.letterbackend.letter_backend.repository.UserRepository;
import com.example.letterbackend.letter_backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // ✅ CORS 허용
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ 회원가입 (name 필드 추가)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // 비밀번호 암호화
        userRepository.save(user);
        return ResponseEntity.ok("회원가입 성공!");
    }

    // ✅ 로그인 (JWT 발급 후 반환)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()
                && passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {

            String token = jwtUtil.generateToken(existingUser.get().getUsername());
            return ResponseEntity.ok(token); // ✅ JWT 토큰 반환
        }

        return ResponseEntity.badRequest().body("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
    }

    // ✅ 현재 로그인된 사용자 정보 조회 (Authorization 헤더 사용)
    @GetMapping("/me")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        String username = jwtUtil.validateToken(token.replace("Bearer ", ""));
        if (username == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        Optional<User> user = userRepository.findByUsername(username);
        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("유저 정보를 찾을 수 없습니다."));
    }
}
