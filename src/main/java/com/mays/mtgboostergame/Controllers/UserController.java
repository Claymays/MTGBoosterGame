package com.mays.mtgboostergame.Controllers;

import com.mays.mtgboostergame.Data.User;
import com.mays.mtgboostergame.Services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mays.mtgboostergame.Services.UserService.DTOUser;

@NoArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DTOUser {
        private Integer id;
        private String message;
    }

    @PostMapping
    public ResponseEntity<DTOUser> createUser(@RequestParam String username, @RequestParam String password) {
        return userService.create(username, password);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOUser> getUser(@PathVariable Integer id) { return userService.get(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id) { return userService.delete(id); }

    @DeleteMapping
    public void deleteAll() { userService.deleteAll(); }
}
