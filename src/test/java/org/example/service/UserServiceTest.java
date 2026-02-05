package org.example.service;

import org.example.userservice.dao.UserDAO;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.example.userservice.service.UserServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;
    @InjectMocks
    private UserServiceImp userService;

    @Test
    public void createUser_WithValidData_ReturnId(){
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString("dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c");
        User mockUser = new User(uuid,name,email,age);

        when(userDAO.existsByEmail(email)).thenReturn(false);
        when(userDAO.save(any(User.class))).thenReturn(mockUser);

        UUID result = userService.createUser(name,email,age);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userDAO).existsByEmail(email);
        verify(userDAO).save(any(User.class));
    }

    @Test
    public void createUser_withDuplicateEmail_throwsException(){
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;

        when(userDAO.existsByEmail(email)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () ->
            userService.createUser(name, email, age));

        verify(userDAO).existsByEmail(email);
    }

    @Test
    public void createUser_withInvalidName_throwsException(){
        String name = "aleshka";
        String email = "alesha@mail.ru";
        Integer age = 26;

        assertThrows(InvalidNameException.class, () ->
                userService.createUser(name, email, age));
    }

    @Test
    public void updateInfo_withValidData_ReturnId() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString(id);
        User mockUser = new User(uuid,name,email,age);

        when(userDAO.existsById(any(UUID.class))).thenReturn(true);
        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());
        when(userDAO.update(any(User.class))).thenReturn(mockUser);

        UUID result = userService.updateInfo(id, name, email, age);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userDAO).existsById(any(UUID.class));
        verify(userDAO).findByEmail(email);
        verify(userDAO).update(any(User.class));
    }

    @Test
    public void updateInfo_withEmailNotChange_ReturnId() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 26;
        UUID uuid = UUID.fromString(id);
        User mockUser = new User(uuid,name,email,age);

        when(userDAO.existsById(any(UUID.class))).thenReturn(true);
        when(userDAO.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userDAO.update(any(User.class))).thenReturn(mockUser);

        UUID result = userService.updateInfo(id, name, email, age);

        assertNotNull(result);
        assertEquals(uuid, result);

        verify(userDAO).existsById(any(UUID.class));
        verify(userDAO).findByEmail(email);
        verify(userDAO).update(any(User.class));
    }

    @Test
    public void updateInfo_withInvalidEmail_throwsException() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mailru";
        Integer age = 26;

        when(userDAO.existsById(any(UUID.class))).thenReturn(true);

        assertThrows(InvalidEmailException.class, () ->
                userService.updateInfo(id,name,email,age));

        verify(userDAO).existsById(any(UUID.class));
    }

    @Test
    public void updateInfo_withInvalidAge_throwsException() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        String name = "Alexey";
        String email = "alesha@mail.ru";
        Integer age = 112;

        when(userDAO.existsById(any(UUID.class))).thenReturn(true);
        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(InvalidAgeException.class, () ->
                userService.updateInfo(id,name,email,age));

        verify(userDAO).existsById(any(UUID.class));
        verify(userDAO).findByEmail(email);
    }

    @Test
    public void findById_withValidId_ReturnUser() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);
        Optional<User> user = Optional.of(new User("Alexey", "alesha@mail.ru",23));

        when(userDAO.findById(uuid)).thenReturn(user);

        Optional<User> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(user, result);

        verify(userDAO).findById(uuid);
    }

    @Test
    public void findById_withInvalidId_throwsException() {
        String id = "dd51-4deb-4f2a-b9a4-8e9b5f";

        assertThrows(InvalidIdException.class, () ->
                userService.findById(id));

    }

    @Test
    public void findById_UserNotFound_ReturnOptionalEmpty() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);

        when(userDAO.findById(uuid)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(id);

        assertTrue(result.isEmpty());

        verify(userDAO).findById(uuid);
    }

    @Test
    public void findByEmail_withValidEmail_ReturnUser() {
        String email = "misyekozhevnikus@yandex.ru";
        Optional<User> user = Optional.of(new User("Max", "misyekozhevnikus@yandex.ru",23));

        when(userDAO.findByEmail(email)).thenReturn(user);

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user, result);

        verify(userDAO).findByEmail(email);
    }

    @Test
    public void findByEmail_withInvalidEmail_ReturnUser() {
        String email = "misyekozhevnikusyandex.ru";

        assertThrows(InvalidEmailException.class, () ->
                userService.findByEmail(email));
    }

    @Test
    public void findByEmail_UserNotFound_ReturnEmptyOptional() {
        String email = "nemaxon@yandex.ru";

        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isEmpty());

        verify(userDAO).findByEmail(email);
    }

    @Test
    public void deleteUser_withValidId_RetrunTrue() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);
        Optional<User> user = Optional.of(new User(uuid,"Max", "maxon@yandex.ru",23));

        when(userDAO.existsById(uuid))
                .thenReturn(true)
                .thenReturn(false);
        when(userDAO.findById(uuid)).thenReturn(user);
        doNothing().when(userDAO).delete(any(User.class));

        boolean result = userService.deleteUser(id);

        assertTrue(result);

        verify(userDAO, times(2)).existsById(uuid);
        verify(userDAO).findById(uuid);
        verify(userDAO).delete(any(User.class));
    }

    @Test
    public void deleteUser_UserNotExists_throwsException() {
        String id = "dd514fb8-4deb-4f2a-b9a4-8e9b5f2db93c";
        UUID uuid = UUID.fromString(id);

        when(userDAO.existsById(uuid)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(id));
    }

}
