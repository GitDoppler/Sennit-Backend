package com.example.sennit.controller;


import com.example.sennit.dto.request.CreatePostRequestDTO;
import com.example.sennit.dto.request.DeletePostRequestDTO;
import com.example.sennit.dto.request.EditPostRequestDTO;
import com.example.sennit.dto.response.*;
import com.example.sennit.dto.request.VotePostRequestDTO;
import com.example.sennit.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/posts/top")
    public ResponseEntity<TopPostsResponseDTO> getTopPosts(@RequestHeader(name = "X-Session-ID") String sessionID){
        return postService.getTopPosts(sessionID);
    }

    @PutMapping("/api/posts/vote")
    public ResponseEntity<VotePostResponseDTO> votePost(@RequestHeader(name = "X-Session-ID") String sessionID, @Valid @RequestBody VotePostRequestDTO votePostRequestDTO){
        return postService.votePost(sessionID, votePostRequestDTO);
    }

    @PostMapping("/api/posts/create")
    public ResponseEntity<CreatePostResponseDTO> createPost(@RequestHeader(name="X-Session-ID") String sessionID, @Valid @RequestBody CreatePostRequestDTO createPostRequestDTO){
        return postService.createPost(sessionID,createPostRequestDTO);
    }

    @DeleteMapping("/api/posts/delete")
    public ResponseEntity<DeletePostResponseDTO> deletePost(@RequestHeader(name="X-Session-ID") String sessionID, @Valid @RequestBody DeletePostRequestDTO deletePostRequestDTO){
        return postService.deletePost(sessionID, deletePostRequestDTO);
    }

    @PutMapping("/api/posts/edit")
    public ResponseEntity<EditPostResponseDTO> editPost(@RequestHeader(name="X-Session-ID") String sessionID, @Valid @RequestBody EditPostRequestDTO editPostRequestDTO){
        return postService.editPost(sessionID, editPostRequestDTO);
    }
}
