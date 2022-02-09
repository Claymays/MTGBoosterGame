package com.mays.mtgboostergame.user;

import com.mays.mtgboostergame.deck.Deck;
import com.mays.mtgboostergame.security.jwt.JwtUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    URI uri;

    @Data
    @NoArgsConstructor
    public static class DTOUser {
        private String token;
        private Integer id;
        private String username;
        private List<Deck> decks;

        public DTOUser(User user, String token) {
            this.token = token;
            this.id = user.getId();
            this. username = user.getUsername();
            this.decks = user.getDecks();
        }
    }

    @Data
    private static class UserRequestBody {
        String username;
        String password;
    }

    @PostMapping
    public ResponseEntity<DTOUser> createUser(@RequestBody UserRequestBody newUser) {
        if (userService.userExistsByUsername(newUser.username)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = userService.create(newUser.username, newUser.password);
        if (user.isPresent()) {
            var userDetails = userService.loadUserByUsername(newUser.username);
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            DTOUser dtoUser = new DTOUser(user.get(), jwtUtil.generateToken(userDetails));
            return ResponseEntity
                    .created(uri)
                    .body(dtoUser);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public  ResponseEntity<DTOUser> getByUsername(@RequestBody UserRequestBody authUser) {
        Optional<User> optUser = userService.getUserByUsername(authUser.username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getPassword().equals(authUser.password)) {
                var details = userService.loadUserByUsername(authUser.username);
                return ResponseEntity.ok(new DTOUser(user, jwtUtil.generateToken(details)));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public void deleteAll() { userService.deleteAll(); }
}
