package org.example.service;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.example.userservice.service.UserServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImp userService;

    @Test
    public void createUser_WithValidData_ReturnId(){
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString("dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c");
        User mockUser = new User(uuid,name,email,age);
        UserDTO userDTO = UserDTO.withId(uuid, name, email, age);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UUID result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userRepository).existsByEmail(email);
        verify(userMapper).toEntity(userDTO);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUser_withDuplicateEmail_throwsException(){
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UserDTO userDTO = UserDTO.create(name, email, age);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () ->
            userService.createUser(userDTO));

        verify(userRepository).existsByEmail(email);
    }

    @Test
    public void createUser_withInvalidName_throwsException(){
        String name = "aleshka";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UserDTO userDTO = UserDTO.create(name, email, age);

        assertThrows(InvalidNameException.class, () ->
                userService.createUser(userDTO));
    }

    @Test
    public void updateInfo_withValidData_ReturnId() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString(id);
        User mockUser = new User(uuid,name,email,age);
        UserDTO userDTO = UserDTO.withId(uuid, name, email, age);

        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UUID result = userService.updateInfo(userDTO);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userRepository).existsById(any(UUID.class));
        verify(userRepository).findByEmail(email);
        verify(userMapper).toEntity(userDTO);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateInfo_withEmailNotChange_ReturnId() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString(id);
        User mockUser = new User(uuid,name,email,age);
        UserDTO userDTO = UserDTO.withId(uuid, name, email, age);

        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UUID result = userService.updateInfo(userDTO);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userRepository).existsById(any(UUID.class));
        verify(userRepository).findByEmail(email);
        verify(userMapper).toEntity(userDTO);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateInfo_withInvalidEmail_throwsException() {
        UUID id = UUID.fromString("dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c");
        String name = "Alexey";
        String email = "alesha@mailru";
        Integer age = 26;
        UserDTO userDTO = UserDTO.withId(id, name, email, age);

        when(userRepository.existsById(any(UUID.class))).thenReturn(true);

        assertThrows(InvalidEmailException.class, () ->
                userService.updateInfo(userDTO));

        verify(userRepository).existsById(any(UUID.class));
    }

    @Test
    public void updateInfo_withInvalidAge_throwsException() {
        UUID id = UUID.fromString("dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c");
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 112;
        UserDTO userDTO = UserDTO.withId(id, name, email, age);

        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(InvalidAgeException.class, () ->
                userService.updateInfo(userDTO));

        verify(userRepository).existsById(any(UUID.class));
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void findById_withValidId_ReturnUser() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);
        Optional<User> user = Optional.of(new User("Alexey", "alesha@mail.ru",23));

        when(userRepository.findById(uuid)).thenReturn(user);

        UserDTO result = userService.findById(id);

        assertNotNull(result);
        assertEquals(user.get().getName(), result.name());
        assertEquals(user.get().getEmail(), result.email());
        assertEquals(user.get().getAge(), result.age());


        verify(userRepository).findById(uuid);
        verify(userMapper).toDTO(user.get());
    }

    @Test
    public void findById_withInvalidId_throwsException() {
        String id = "dd51-4deb-4f2a-b9a4-8e9b5f";

        assertThrows(InvalidIdException.class,
                () -> userService.findById(id));

    }

    @Test
    public void findById_UserNotFound_throwsException() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);

        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(id));

        verify(userRepository).findById(uuid);
    }


    @Test
    public void deleteUser_withValidId_RetrunTrue() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);
        Optional<User> user = Optional.of(new User(uuid,"Max", "maxon@yandex.ru",23));

        when(userRepository.existsById(uuid))
                .thenReturn(true)
                .thenReturn(false);
        when(userRepository.findById(uuid)).thenReturn(user);
        doNothing().when(userRepository).delete(any(User.class));

        boolean result = userService.deleteUser(id);

        assertTrue(result);

        verify(userRepository, times(2)).existsById(uuid);
        verify(userRepository).findById(uuid);
        verify(userRepository).delete(any(User.class));
    }

    @Test
    public void deleteUser_UserNotExists_throwsException() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);

        when(userRepository.existsById(uuid)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(id));
    }

}
