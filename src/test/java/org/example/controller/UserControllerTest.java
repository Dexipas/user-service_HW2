package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.userservice.App;
import org.example.userservice.controller.UserController;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.exception.InvalidEmailException;
import org.example.userservice.exception.InvalidIdException;
import org.example.userservice.exception.InvalidNameException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;


@WebMvcTest(UserController.class)
@ContextConfiguration(classes = App.class)
public class UserControllerTest {
    private static final UUID USER_ID = UUID.fromString("e1732746-f2d8-4d6b-be38-3822814d5362");
    private static final String USER_NAME = "Egor";
    private static final String USER_EMAIL = "egorushkamyau@mail.ru";
    private static final Integer USER_AGE = 27;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void create_WithValidData_Response201WithId() throws Exception {
        UserDTO userDTO = UserDTO.create(USER_NAME, USER_EMAIL, USER_AGE);

        when(userService.createUser(any(UserDTO.class))).thenReturn(USER_ID);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(USER_ID.toString()));
    }

    @Test
    public void create_WithInvalidName_Response400BadRequest() throws Exception {
        UserDTO userDTO = UserDTO.create("dimon228", USER_EMAIL, USER_AGE);

        when(userService.createUser(any(UserDTO.class))).thenThrow(InvalidNameException.class);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_WithValidData_Response200WithId() throws Exception {
        UserDTO userDTO = UserDTO.withId(USER_ID, USER_NAME, USER_EMAIL, USER_AGE);

        when(userService.updateInfo(any(UserDTO.class))).thenReturn(USER_ID);

        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(USER_ID.toString()));
    }

    @Test
    public void update_WithNotExistsUser_Response404UserNotFound() throws Exception {
        UserDTO userDTO = UserDTO.withId(USER_ID, USER_NAME, USER_EMAIL, USER_AGE);

        when(userService.updateInfo(any(UserDTO.class))).thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_WithIncorrectEmail_Response400BadRequest() throws Exception {
        UserDTO userDTO = UserDTO.withId(USER_ID, USER_NAME, "egr@mail.r.u", USER_AGE);

        when(userService.updateInfo(any(UserDTO.class))).thenThrow(InvalidEmailException.class);

        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_WithCorrectId_Response200WithTrue() throws Exception {
        when(userService.deleteUser(USER_ID.toString())).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    public void delete_WithIncorrectId_Response400BadRequest() throws Exception {
        String id = "e1732746-f2d8-4d6b-be383822814d5362";

        when(userService.deleteUser(id)).thenThrow(InvalidIdException.class);

        mockMvc.perform(delete("/api/users/{id}",id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findById_WithCorrectId_ResponseOkWithUserDTO() throws Exception {
        UserDTO userDTO = UserDTO.withId(USER_ID, USER_NAME, USER_EMAIL, USER_AGE);

        when(userService.findById(USER_ID.toString())).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.age").value(USER_AGE));
    }

    @Test
    public void findById_WithUserNotExists_Response404UserNotFound() throws Exception {
        UserDTO userDTO = UserDTO.withId(USER_ID, USER_NAME, USER_EMAIL, USER_AGE);

        when(userService.findById(USER_ID.toString())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/api/users/{id}",USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAll_withTwoUsers_ResponseOkWithTwoUsers() throws Exception {
        UUID uuid = UUID.fromString("e1733746-f2c8-4d6b-be38-3822814d5362");
        List<UserDTO> users = List.of(
                UserDTO.withId(USER_ID, USER_NAME, USER_EMAIL, USER_AGE),
                UserDTO.withId(uuid,"Fillip","filip@ya.ru",17));

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value(USER_NAME))
                .andExpect(jsonPath("$[1].name").value("Fillip"));
    }

}
