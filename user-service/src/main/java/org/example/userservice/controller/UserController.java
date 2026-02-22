package org.example.userservice.controller;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {
        public final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody @Validated UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping
    public UserDTO update(@RequestBody @Validated UserDTO userDTO) {
        return userService.updateInfo(userDTO);
    }

    @DeleteMapping(path = "/{id}")
    public boolean delete(@PathVariable String id){
        return userService.deleteUser(id);
    }

    @GetMapping(path = "/{id}")
    public UserDTO findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }


}
