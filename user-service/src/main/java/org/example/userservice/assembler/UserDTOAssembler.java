package org.example.userservice.assembler;

import org.example.userservice.controller.UserController;
import org.example.userservice.dto.UserDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserDTOAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public EntityModel<UserDTO> toModel(UserDTO userDTO) {
        return EntityModel.of(userDTO,
                linkTo(methodOn(UserController.class).create(userDTO)).withRel("save"),
                linkTo(methodOn(UserController.class).update(userDTO)).withRel("update"),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"),
                linkTo(methodOn(UserController.class).findById(userDTO.id().toString())).withRel("findById"),
                linkTo(methodOn(UserController.class).findByEmail(userDTO.email())).withRel("findByEmail"),
                linkTo(methodOn(UserController.class).delete(userDTO.id().toString())).withRel("delete")
                );
    }

//    public EntityModel<UserDTO> toModel
}
