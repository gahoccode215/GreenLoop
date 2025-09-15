package com.greenloop.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info(HttpServletRequest request) {
        // Lấy user info từ header do Gateway forward xuống
        String userId = request.getHeader("X-User-Id");

        if (userId == null) {
            log.warn("No user info found in request headers");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        log.info("User info request from: {}", userId);

        Map<String, String> userInfo = Map.of(
                "userId", userId,
                "message", "User authenticated successfully",
                "timestamp", String.valueOf(System.currentTimeMillis())
        );

        return ResponseEntity.ok(userInfo);
    }
}

