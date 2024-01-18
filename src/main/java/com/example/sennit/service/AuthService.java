package com.example.sennit.service;

import com.example.sennit.dto.request.UserSignUpRequestDTO;
import com.example.sennit.dto.request.UserSignInRequestDTO;
import com.example.sennit.dto.response.UserSignInResponseDTO;
import com.example.sennit.dto.response.UserSignUpResponseDTO;
import com.example.sennit.model.Reputation;
import com.example.sennit.model.Role;
import com.example.sennit.model.Session;
import com.example.sennit.model.User;
import com.example.sennit.repository.AuthRepository;
import com.example.sennit.repository.RepRepository;
import com.example.sennit.repository.RoleRepository;
import com.example.sennit.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RepRepository repRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public AuthService(AuthRepository authRepository, UserRepository userRepository, RoleRepository roleRepository, RepRepository repRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.repRepository = repRepository;
    }

    @Transactional
    public ResponseEntity<UserSignUpResponseDTO> signUp(UserSignUpRequestDTO userSignUpRequestDTO){
        try {
            // Create a new user
            String encodedPassword = bCryptPasswordEncoder.encode(userSignUpRequestDTO.password());
            User newUser = new User();
            newUser.setUsername(userSignUpRequestDTO.username());
            newUser.setPasswordHash(encodedPassword);
            newUser.setEmail(userSignUpRequestDTO.email());
            User savedUser = userRepository.save(newUser);

            //Set reputation to 0
            Reputation rep=new Reputation();
            rep.setUserID(savedUser.getUserID());
            rep.setReputationScore(0);
            repRepository.save(rep);

            //Set role to NORMAL
            Role newUserRole=new Role();
            newUserRole.setUserID(savedUser.getUserID());
            newUserRole.setRoleTypeID(1L); // 1 <- NORMAL role
            roleRepository.save(newUserRole);

            //Create session
            Session newSession = new Session();
            newSession.setUserID(savedUser.getUserID());
            newSession.setStringID(UUID.randomUUID().toString());
            Session savedSession=authRepository.save(newSession);

            UserSignUpResponseDTO userSignUpResponseDTO= new UserSignUpResponseDTO("success","User has been signed up",Optional.of(savedSession.getStringID()),Optional.of(newUser.getUsername()),Optional.of(getRoleList(newUser.getUserID())));
            return new ResponseEntity<>(userSignUpResponseDTO, HttpStatus.OK);
        }catch (DataIntegrityViolationException e) {
            String cause = e.getMostSpecificCause().getMessage();

            String errorMessage;
            HttpStatus status;
            if (cause.contains("username")) {
                errorMessage = "Username already exists";
                status = HttpStatus.CONFLICT;
            } else if (cause.contains("email")) {
                errorMessage = "Email already exists";
                status = HttpStatus.CONFLICT;
            } else {
                errorMessage = "Data integrity violation";
                status = HttpStatus.BAD_REQUEST;
            }

            return new ResponseEntity<>(new UserSignUpResponseDTO("error",errorMessage,Optional.empty(),Optional.empty(),Optional.empty()), status);
        } catch (Exception e) {
            return new ResponseEntity<>(new UserSignUpResponseDTO("error","An unexpected error occured",Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<UserSignInResponseDTO> signIn(UserSignInRequestDTO userSignInRequestDTO){
        // Search for user based on email
        User currentUser=userRepository.findUserByEmail(userSignInRequestDTO.email());
        if (currentUser == null || !bCryptPasswordEncoder.matches(userSignInRequestDTO.password(), currentUser.getPasswordHash())) {
            return new ResponseEntity<>(new UserSignInResponseDTO("error","Invalid email or password",Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.UNAUTHORIZED);
        }

        // Check for existing session
        Session existingSession = authRepository.findSessionByUserID(currentUser.getUserID());
        Session savedSession;
        if (existingSession != null) {
            // Update existing session with new session ID
            existingSession.setStringID(UUID.randomUUID().toString());
            existingSession.setExpirationDate(LocalDateTime.now().plusMinutes(30));
            savedSession=authRepository.save(existingSession);
        }else{
            // Create a new session
            Session newSession = new Session();
            newSession.setUserID(currentUser.getUserID());
            newSession.setStringID(UUID.randomUUID().toString());
            savedSession=authRepository.save(newSession);
        }

        return new ResponseEntity<>(new UserSignInResponseDTO("success","User has been signed in", Optional.of(savedSession.getStringID()), Optional.of(currentUser.getUsername()), Optional.of(getRoleList(currentUser.getUserID())) ), HttpStatus.OK);
    }

    public List<String> getRoleList(Long userID){
        List<Object[]> roles=roleRepository.findRoleByUserID(userID);
        List<String> roleList = new ArrayList<>();
        for(Object[] role:roles){
            roleList.add((String) role[1]);
        }
        return roleList;
    }

    public Boolean verifySession(String sessionID, Long userID){
        Session currentSession = authRepository.findSessionByUserID(userID);
        if(currentSession==null){
            return false;
        }
        return Objects.equals(currentSession.getStringID(), sessionID);
    }

    public String refreshSession(Long userID){
        Session currentSession=authRepository.findSessionByUserID(userID);
        currentSession.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        Session savedSession=authRepository.save(currentSession);
        return savedSession.getStringID();
    }
    
}
