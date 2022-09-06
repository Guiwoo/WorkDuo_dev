package com.workduo.configuration.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Testing 을 위한 api 작성 member api 작성시 삭제 예정

@RestController
@RequestMapping("/api/v1")
public class SecurityApi {

    @GetMapping("/login")
    public ResponseEntity<?> getLogin(){
        return ResponseEntity.status(200).body("");
    }

    @GetMapping("/auth")
    public ResponseEntity<?> getAuth(){
        return ResponseEntity.status(200).body("");
    }
}
