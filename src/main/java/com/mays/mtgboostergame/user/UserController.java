package com.mays.mtgboostergame.user;

import com.google.gson.Gson;
import com.mays.mtgboostergame.deck.Deck;
import com.mays.mtgboostergame.security.jwt.JwtUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private Gson gson = new Gson();
    URI uri;

    @Data
    @NoArgsConstructor
//    A data transfer object for users.
    public static class DTOUser {
        private Integer id;
        private String username;
        private List<Deck> decks;

        public DTOUser(User user) {
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
    public ResponseEntity<DTOUser> getUser() {
        var context = SecurityContextHolder.getContext().getAuthentication().getName();
        var optUser = userService.getUserByUsername(context);

        if (optUser.isPresent()) {
            var user = optUser.get();
            var dtoUser = new DTOUser(user);
            return ResponseEntity.ok(dtoUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequestBody newUser) {
        if (userService.userExistsByUsername(newUser.username)) {
            return ResponseEntity.badRequest().build();
        }

        var encryptedPassword = passwordEncoder.encode(newUser.password);
        Optional<User> user = userService.create(newUser.username, encryptedPassword);

        if (user.isPresent()) {
            var userDetails = userService.loadUserByUsername(newUser.username);
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity
                    .created(uri)
                    .body(gson.toJson(jwtUtil.generateToken(userDetails)));
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public  ResponseEntity<String> getByUsername(@RequestBody UserRequestBody authUser) {
        Optional<User> optUser = userService.getUserByUsername(authUser.username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(authUser.password, user.getPassword())) {
                var details = userService.loadUserByUsername(authUser.username);
                return ResponseEntity.ok(gson.toJson(jwtUtil.generateToken(details)));
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
