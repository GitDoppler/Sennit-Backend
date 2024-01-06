package com.example.sennit.controller;

import com.example.sennit.dto.response.UserWithPostsResponseDTO;
import com.example.sennit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/api/users/{username}")
    public ResponseEntity<UserWithPostsResponseDTO> findByProfileName(@RequestHeader(name = "X-Session-ID") String sessionID,@PathVariable String username){ return userService.findUserByUsername(sessionID,username);}
}
