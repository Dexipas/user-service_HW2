package org.example.feign;

import org.example.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Component
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/email/{email}")
    EntityModel<UserDTO> findByEmail(@PathVariable("email") String email);
}
