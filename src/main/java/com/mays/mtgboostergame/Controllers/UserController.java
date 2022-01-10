package com.mays.mtgboostergame.Controllers;

import com.mays.mtgboostergame.Services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@NoArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    URI uri;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DTOUser {
        private Integer id;
        private String username;
    }

    @PostMapping
    public ResponseEntity<DTOUser> createUser(@RequestParam String username, @RequestParam String password) {
        uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(new DTOUser(userService.create(username, password).get().getId(), username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOUser> getUser(@PathVariable Integer id) { return ResponseEntity.ok().body(new DTOUser(userService.get(id).get().getId(), userService.getUser(id).get().getUsername()) ); }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public void deleteAll() { userService.deleteAll(); }
}
