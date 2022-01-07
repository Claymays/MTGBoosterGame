package com.mays.mtgboostergame.Services;

import com.mays.mtgboostergame.Data.User;
import com.mays.mtgboostergame.Data.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@Service
@AllArgsConstructor
public class UserService {
    @Autowired private UserRepository userRepository;

    public Optional<User> create(String username, String password) {
        return Optional.of(userRepository.save(new User(username, password)));
    }

    public Optional<User> get(Integer id) {
       return userRepository.findById(id);
    }

    // TODO: Figure out how to return all of something.
//    public ResponseEntity<List<DTOUser>>

    public Optional<User> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
