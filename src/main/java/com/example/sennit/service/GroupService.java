package com.example.sennit.service;

import com.example.sennit.dto.request.*;
import com.example.sennit.dto.response.*;
import com.example.sennit.model.Group;
import com.example.sennit.model.Session;
import com.example.sennit.model.User;
import com.example.sennit.repository.AuthRepository;
import com.example.sennit.repository.GroupRepository;
import com.example.sennit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    public GroupService(GroupRepository groupRepository, AuthRepository authRepository, UserRepository userRepository, AuthService authService) {
        this.groupRepository = groupRepository;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public ResponseEntity<GetGroupResponseDTO> getGroup(String sessionID,GetGroupRequestDTO getGroupRequestDTO){
        // Verify if input is valid
        if(getGroupRequestDTO==null){
            return new ResponseEntity<>(new GetGroupResponseDTO("error","Invalid input", Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.BAD_REQUEST);
        }

        // Authenticate session
        Session session = authRepository.findSessionByStringID(sessionID);
        if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new GetGroupResponseDTO("error","Session not valid", Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty()), HttpStatus.UNAUTHORIZED);
        }

        // Find subgroup by name
        Group group =groupRepository.findGroupByName(getGroupRequestDTO.name());
        if(group ==null){
            GetGroupResponseDTO response=new GetGroupResponseDTO("error","Subgroup not found", Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty());
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

        List<String> listMembers=new ArrayList<>();
        boolean isMember=false;
        for(User member: group.getMembers()){
            listMembers.add(member.getUsername());
            if(Objects.equals(member.getUserID(), session.getUserID())){
                isMember=true;
            }
        }
        GetGroupResponseDTO response=new GetGroupResponseDTO("success","Subgroup has been found", Optional.of(group.getName()),Optional.of(group.getDescription()),Optional.of(group.getCreatedAt()),Optional.of(listMembers),Optional.of(isMember));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CreateGroupResponseDTO> createGroup(String sessionID,CreateGroupRequestDTO createGroupRequestDTO){
        // Verify if input is valid
        if(createGroupRequestDTO ==null){
            return new ResponseEntity<>(new CreateGroupResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new CreateGroupResponseDTO("error","Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find user
            User user=userRepository.findUserByUserID(session.getUserID());
            if(user==null){
                return new ResponseEntity<>(new CreateGroupResponseDTO("error","User can't be found"), HttpStatus.NOT_FOUND);
            }

            // Check if group name already exists
            Group group= groupRepository.findGroupByName(createGroupRequestDTO.name());
            if(group!=null){
                return new ResponseEntity<>(new CreateGroupResponseDTO("error","Group name has been taken"), HttpStatus.CONFLICT);
            }
            Group newGroup=new Group();
            newGroup.setName(createGroupRequestDTO.name());
            newGroup.setDescription(createGroupRequestDTO.description());
            newGroup.setOwnerID(session.getUserID());
            Group savedGroup=groupRepository.save(newGroup);

            List<Group> listGroups =user.getListGroups();
            listGroups.add(savedGroup);
            user.setListGroups(listGroups);
            userRepository.save(user);

            return new ResponseEntity<>(new CreateGroupResponseDTO("success","Group has been created"), HttpStatus.CREATED);
        }catch(Exception e){
            // Log the exception for debugging
            System.out.println("Error while processing vote: "+ e);

            return new ResponseEntity<>(new CreateGroupResponseDTO("error","Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<JoinGroupResponseDTO> joinGroup(String sessionID, JoinGroupRequestDTO joinGroupRequestDTO){
        // Verify if input is valid
        if(joinGroupRequestDTO==null){
            return new ResponseEntity<>(new JoinGroupResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new JoinGroupResponseDTO("error","Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find user
            User user=userRepository.findUserByUserID(session.getUserID());
            if(user==null){
                return new ResponseEntity<>(new JoinGroupResponseDTO("error","User can't be found"), HttpStatus.NOT_FOUND);
            }

            // Find group
            Group group=groupRepository.findGroupByName(joinGroupRequestDTO.name());
            if(group==null){
                return new ResponseEntity<>(new JoinGroupResponseDTO("error","Group can't be found"), HttpStatus.NOT_FOUND);
            }

            if(user.getListGroups().contains(group)){
                return new ResponseEntity<>(new JoinGroupResponseDTO("error","User is already a member"), HttpStatus.CONFLICT);
            }
            user.getListGroups().add(group);
            userRepository.save(user);

            return new ResponseEntity<>(new JoinGroupResponseDTO("success","User has joined the group"), HttpStatus.OK);
        }catch(Exception e){
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new JoinGroupResponseDTO("error","Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<DeleteGroupResponseDTO> deleteGroup(String sessionID,DeleteGroupRequestDTO deleteGroupRequestDTO){
        // Verify if input is valid
        if(deleteGroupRequestDTO==null){
            return new ResponseEntity<>(new DeleteGroupResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new DeleteGroupResponseDTO("error","Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find group
            Group group=groupRepository.findGroupByName(deleteGroupRequestDTO.name());
            if(group==null){
                return new ResponseEntity<>(new DeleteGroupResponseDTO("error","Group can't be found"), HttpStatus.NOT_FOUND);
            }

            // Validate ownership and check if ADMIN
            List<String> roleList=authService.getRoleList(session.getUserID());
            if(Objects.equals(session.getUserID(), group.getOwnerID())||roleList.contains("ADMIN")){
                groupRepository.delete(group);
                return new ResponseEntity<>(new DeleteGroupResponseDTO("success","Group has been deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new DeleteGroupResponseDTO("error","Session doesn't match"), HttpStatus.UNAUTHORIZED);
        }catch(Exception e){
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new DeleteGroupResponseDTO("error","Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<EditGroupResponseDTO> editGroup(String sessionID, EditGroupRequestDTO editGroupRequestDTO){
        // Verify if input is valid
        if(editGroupRequestDTO==null){
            return new ResponseEntity<>(new EditGroupResponseDTO("error","Invalid input"), HttpStatus.BAD_REQUEST);
        }

        try{
            // Authenticate session
            Session session = authRepository.findSessionByStringID(sessionID);
            if (session == null || session.getExpirationDate().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(new EditGroupResponseDTO("error","Session not valid"), HttpStatus.UNAUTHORIZED);
            }

            // Find group
            Group group=groupRepository.findGroupByGroupID(editGroupRequestDTO.groupID());
            if(group==null){
                return new ResponseEntity<>(new EditGroupResponseDTO("error","Group can't be found"), HttpStatus.NOT_FOUND);
            }

            // Validate ownership and check if ADMIN
            List<String> roleList=authService.getRoleList(session.getUserID());
            if(!Objects.equals(session.getUserID(), group.getOwnerID()) && !roleList.contains("ADMIN")){
                return new ResponseEntity<>(new EditGroupResponseDTO("error","Session doesn't match"), HttpStatus.UNAUTHORIZED);
            }

            // Check if name is taken
            Group possibleGroup=groupRepository.findGroupByName(editGroupRequestDTO.name());
            if(possibleGroup!=null && !Objects.equals(possibleGroup.getGroupID(), editGroupRequestDTO.groupID())){
                return new ResponseEntity<>(new EditGroupResponseDTO("error","Group name has been taken"), HttpStatus.CONFLICT);
            }

            group.setName(editGroupRequestDTO.name());
            group.setDescription(editGroupRequestDTO.description());
            groupRepository.save(group);
            return new ResponseEntity<>(new EditGroupResponseDTO("success","Group has been edited"), HttpStatus.OK);
        }catch(Exception e){
            // Log the exception for debugging
            // e.g., logger.error("Error while processing vote: ", e);

            return new ResponseEntity<>(new EditGroupResponseDTO("error","Internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
