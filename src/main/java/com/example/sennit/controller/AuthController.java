package com.example.sennit.controller;

import com.example.sennit.dto.request.UserSignUpRequestDTO;
import com.example.sennit.dto.request.UserSignInRequestDTO;
import com.example.sennit.dto.response.UserSignInResponseDTO;
import com.example.sennit.dto.response.UserSignUpResponseDTO;
import com.example.sennit.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/signup")
    public ResponseEntity<UserSignUpResponseDTO> signUp(@Valid @RequestBody UserSignUpRequestDTO userSignUpRequestDTO){
        return authService.signUp(userSignUpRequestDTO);
    }

    @PostMapping("/api/signin")
    public ResponseEntity<UserSignInResponseDTO> signIn(@Valid @RequestBody UserSignInRequestDTO userSignInRequestDTO){
        return authService.signIn(userSignInRequestDTO);
    }
}
