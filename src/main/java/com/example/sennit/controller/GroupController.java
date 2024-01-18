package com.example.sennit.controller;

import com.example.sennit.dto.request.*;
import com.example.sennit.dto.response.*;
import com.example.sennit.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/api/group/search")
    public ResponseEntity<GetGroupResponseDTO> getGroup(@RequestHeader(name = "X-Session-ID") String sessionID,@RequestBody GetGroupRequestDTO getGroupRequestDTO){
        return groupService.getGroup(sessionID,getGroupRequestDTO);
    }

    @PostMapping("/api/group/create")
    public ResponseEntity<CreateGroupResponseDTO> createGroup(@RequestHeader(name = "X-Session-ID") String sessionID,@Valid @RequestBody CreateGroupRequestDTO createGroupRequestDTO){
        return groupService.createGroup(sessionID,createGroupRequestDTO);
    }

    @PutMapping("/api/group/join")
    public ResponseEntity<JoinGroupResponseDTO> joinGroup(@RequestHeader(name = "X-Session-ID") String sessionID,@Valid @RequestBody JoinGroupRequestDTO joinGroupRequestDTO){
        return groupService.joinGroup(sessionID,joinGroupRequestDTO);
    }

    @DeleteMapping("/api/group/delete")
    public ResponseEntity<DeleteGroupResponseDTO> deleteGroup(@RequestHeader(name = "X-Session-ID") String sessionID,@Valid @RequestBody DeleteGroupRequestDTO deleteGroupRequestDTO){
        return groupService.deleteGroup(sessionID,deleteGroupRequestDTO);
    }

    @PutMapping("/api/group/edit")
    public ResponseEntity<EditGroupResponseDTO> editGroup(@RequestHeader(name = "X-Session-ID") String sessionID,@Valid @RequestBody EditGroupRequestDTO editGroupRequestDTO){
        return groupService.editGroup(sessionID,editGroupRequestDTO);
    }
}
