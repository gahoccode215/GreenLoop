package com.greenloop.auth.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private")
@RequiredArgsConstructor
@Slf4j
public class PrivateController {
    @GetMapping
    public String getPublic() {
        log.info("get private controller");
        return "private endpoint";
    }
    @GetMapping("/info")
    public String info(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id"); // From Gateway
        return "Private Info - User: " + userId;
    }
}
