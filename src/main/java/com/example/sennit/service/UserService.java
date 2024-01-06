package com.example.sennit.service;

import com.example.sennit.dto.common.PostWithVoteDTO;
import com.example.sennit.dto.response.TopPostsResponseDTO;
import com.example.sennit.dto.response.UserWithPostsResponseDTO;
import com.example.sennit.model.Session;
import com.example.sennit.model.User;
import com.example.sennit.repository.AuthRepository;
import com.example.sennit.repository.PostRepository;
import com.example.sennit.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.authRepository = authRepository;
    }

    public ResponseEntity<UserWithPostsResponseDTO> findUserByUsername(String sessionID,String username){
        // Verify if input is valid
        if(username==null || username.isEmpty()){
            return new ResponseEntity<>(new UserWithPostsResponseDTO("error","Invalid input", Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.BAD_REQUEST);
        }

        // Authenticate session
        Session session=authRepository.findSessionByStringID(sessionID);
        if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new UserWithPostsResponseDTO("error","Session not valid",Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.UNAUTHORIZED);
        }

        // Find the user given a username
        User searchedUser=userRepository.findUserByUsername(username);
        if(searchedUser==null){
            return new ResponseEntity<>(new UserWithPostsResponseDTO("error","User not found", Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.NOT_FOUND);
        }

        // Get top posts with native query and map to DTO
        List<Object[]> results=postRepository.findPostsByUserID(searchedUser.getUserID(), session.getUserID());
        List<PostWithVoteDTO> listPosts= new ArrayList<>();
        for(Object[] result: results){
            PostWithVoteDTO postWithVoteDTO= new PostWithVoteDTO(
                    (Long) result[0],
                    (String) result[1],
                    (String) result[2],
                    LocalDateTime.ofInstant((Instant) result[3], ZoneId.systemDefault()),
                    searchedUser.getUsername(),
                    (Long) result[4], //The query sum comes back as Long/Bigint
                    (Integer) result[5]
            );
            listPosts.add(postWithVoteDTO);
        }

        UserWithPostsResponseDTO userWithPostsResponseDTO=new UserWithPostsResponseDTO("success","User has been queried",Optional.of(searchedUser.getUsername()),Optional.of(searchedUser.getCreatedAt()),Optional.of(listPosts));
        return new ResponseEntity<>(userWithPostsResponseDTO,HttpStatus.OK);
    }
}
