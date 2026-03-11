package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.userservice.assembler.UserDTOAssembler;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/users")
public class UserController {
        public final UserService userService;
        public final UserDTOAssembler userDTOAssembler;

    public UserController(UserService userService, UserDTOAssembler userDTOAssembler) {
        this.userService = userService;
        this.userDTOAssembler = userDTOAssembler;
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Возращает UserDTO созданного пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDTO> create(@RequestBody @Validated @Schema(description = "Данные нового пользователя") UserDTO userDTO) {
        return userDTOAssembler.toModel(userService.createUser(userDTO));
    }

    @PutMapping
    @Operation(summary = "Обновить данные пользователя", description = "Возращает UserDTO обновленного пользователя")
    public EntityModel<UserDTO> update(@RequestBody @Validated @Schema(description = "Данные для обновления пользователя") UserDTO userDTO) {
        return userDTOAssembler.toModel(userService.updateInfo(userDTO));
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удалить пользователя", description = "Возвращает значение boolean, исходя из результата выполнения операции удаления")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор пользователя") @PathVariable String id){
        boolean result = userService.deleteUser(id);
        return result
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/id/{id}")
    @Operation(summary = "Получить пользователя по id", description = "Возвращает пользователя по его идентификатору")
    public EntityModel<UserDTO> findById(@Parameter(description = "Идентификатор пользователя") @PathVariable String id) {
        return userDTOAssembler.toModel(userService.findById(id));
    }

    @GetMapping(path = "/email/{email}")
    @Operation(summary = "Получить пользователя по email", description = "Возвращает пользователя по его email адресу")
    public EntityModel<UserDTO> findByEmail(@Parameter(description = "Email пользователя") @PathVariable String email) {
        return userDTOAssembler.toModel(userService.findByEmail(email));
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей из БД")
    public CollectionModel<EntityModel<UserDTO>> findAll() {
        List<UserDTO> usersDTO = userService.findAll();
        List<EntityModel<UserDTO>> usersModel = usersDTO.stream()
                .map(userDTOAssembler::toModel)
                .toList();
        return CollectionModel.of(usersModel,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }


}
