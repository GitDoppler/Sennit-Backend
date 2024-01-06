package com.example.sennit.service;

import com.example.sennit.dto.common.PostWithVoteDTO;
import com.example.sennit.dto.request.CreatePostRequestDTO;
import com.example.sennit.dto.request.DeletePostRequestDTO;
import com.example.sennit.dto.request.EditPostRequestDTO;
import com.example.sennit.dto.response.*;
import com.example.sennit.dto.request.VotePostRequestDTO;
import com.example.sennit.model.Post;
import com.example.sennit.model.Session;
import com.example.sennit.model.User;
import com.example.sennit.model.Vote;
import com.example.sennit.repository.AuthRepository;
import com.example.sennit.repository.PostRepository;
import com.example.sennit.repository.UserRepository;
import com.example.sennit.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, UserRepository userRepository, AuthRepository authRepository, VoteRepository voteRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.voteRepository = voteRepository;
        this.authService = authService;
    }

    public ResponseEntity<TopPostsResponseDTO> getTopPosts(String sessionID){
        // Verify if input is valid
        if (sessionID == null) {
            return new ResponseEntity<>(new TopPostsResponseDTO("error","Invalid input",Optional.empty()), HttpStatus.BAD_REQUEST);
        }

        // Authenticate session
        Session session=authRepository.findSessionByStringID(sessionID);
        if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new TopPostsResponseDTO("error","Session not valid",Optional.empty()), HttpStatus.UNAUTHORIZED);
        }

        // Find user 
        User currentUser=userRepository.findUserByUserID(session.getUserID());
        if(currentUser==null){
            return new ResponseEntity<>(new TopPostsResponseDTO("error","User not found",Optional.empty()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Get top posts with native query and map to DTO
        List<Object[]> results=postRepository.findTopPostsByUserID(currentUser.getUserID());
        List<PostWithVoteDTO> listPosts= new ArrayList<>();
        for(Object[] result: results){
            PostWithVoteDTO postWithVoteDTO= new PostWithVoteDTO(
                    (Long) result[0],
                    (String) result[1],
                    (String) result[2],
                    LocalDateTime.ofInstant((Instant) result[4], ZoneId.systemDefault()),
                    (String) result[5],
                    (Long) result[6], //The query sum comes back as Long/Bigint
                    (Integer) result[7]
            );
            listPosts.add(postWithVoteDTO);
        }
        return new ResponseEntity<>(new TopPostsResponseDTO("success","Top posts have been queried",Optional.of(listPosts)),HttpStatus.OK);
    }

    public ResponseEntity<VotePostResponseDTO> votePost(String sessionID, VotePostRequestDTO votePostRequestDTO) {
        // Verify if input is valid
        if (sessionID == null || sessionID.isEmpty() || votePostRequestDTO == null) {
            return new ResponseEntity<>(new VotePostResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new VotePostResponseDTO("error","Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Check if the user has already voted
            Vote currentVote = voteRepository.findVoteByUserIDAndPostID(session.getUserID(), votePostRequestDTO.postID());
            if (currentVote == null) {
                currentVote = new Vote();
                currentVote.setPostID(votePostRequestDTO.postID());
            }

            // Update the vote type
            currentVote.setVoteType(votePostRequestDTO.voteType());
            voteRepository.save(currentVote);

            return new ResponseEntity<>(new VotePostResponseDTO("success","Vote has been saved"), HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new VotePostResponseDTO("error","Invalid input"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<CreatePostResponseDTO> createPost(String sessionID, CreatePostRequestDTO createPostDTO){
        // Verify if input is valid
        if (sessionID == null || sessionID.isEmpty() || createPostDTO == null) {
            return new ResponseEntity<>(new CreatePostResponseDTO("error","Invalid input", Optional.empty()), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new CreatePostResponseDTO("error", "Session not valid",Optional.empty()), HttpStatus.UNAUTHORIZED);
            }

            // Create new post with data from DTO
            Post newPost=new Post();
            newPost.setTitle(createPostDTO.title());
            newPost.setContent(createPostDTO.content());
            newPost.setUserID(session.getUserID());
            Post savedPost=postRepository.save(newPost);
            return new ResponseEntity<>(new CreatePostResponseDTO("successful","Post has been created", Optional.of(savedPost.getPostID())),HttpStatus.CREATED);
        }catch (Exception e) {
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new CreatePostResponseDTO("error", "Internal error occurred",Optional.empty()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<DeletePostResponseDTO> deletePost(String sessionID, DeletePostRequestDTO deletePostRequestDTO){
        // Verify if input is valid
        if (sessionID == null || sessionID.isEmpty() || deletePostRequestDTO == null) {
            return new ResponseEntity<>(new DeletePostResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new DeletePostResponseDTO("error", "Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find post based on postID
            Post post=postRepository.findPostByPostID(deletePostRequestDTO.postID());
            if(post==null){
                return new ResponseEntity<>(new DeletePostResponseDTO("error","Post can't be found"),HttpStatus.NOT_FOUND);
            }

            // Validate ownership and check if ADMIN
            List<String> listRoles=authService.getRoleList(session.getUserID());
            if(Objects.equals(post.getUserID(), session.getUserID()) || listRoles.contains("ADMIN")){
                postRepository.deleteById(deletePostRequestDTO.postID());
                return new ResponseEntity<>(new DeletePostResponseDTO("success","Post has been deleted"),HttpStatus.OK);
            }
            return new ResponseEntity<>(new DeletePostResponseDTO("error", "Session doesn't match"), HttpStatus.UNAUTHORIZED);

        }catch(Exception e){
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new DeletePostResponseDTO("error", "Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<EditPostResponseDTO> editPost(String sessionID, EditPostRequestDTO editPostRequestDTO){
        // Verify if input is valid
        if (sessionID == null || sessionID.isEmpty() || editPostRequestDTO == null) {
            return new ResponseEntity<>(new EditPostResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new EditPostResponseDTO("error", "Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find post based on postID
            Post post=postRepository.findPostByPostID(editPostRequestDTO.postID());
            if(post==null){
                return new ResponseEntity<>(new EditPostResponseDTO("error","Post can't be found"),HttpStatus.NOT_FOUND);
            }

            // Validate ownership and check if ADMIN
            List<String> listRoles=authService.getRoleList(session.getUserID());
            if(Objects.equals(post.getUserID(), session.getUserID()) || listRoles.contains("ADMIN")){
                post.setTitle(editPostRequestDTO.title());
                post.setContent(editPostRequestDTO.content());
                postRepository.save(post);
                return new ResponseEntity<>(new EditPostResponseDTO("success","Post has been edited"),HttpStatus.OK);
            }
            return new ResponseEntity<>(new EditPostResponseDTO("error", "Session doesn't match"), HttpStatus.UNAUTHORIZED);
        }catch(Exception e){
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new EditPostResponseDTO("error", "Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
