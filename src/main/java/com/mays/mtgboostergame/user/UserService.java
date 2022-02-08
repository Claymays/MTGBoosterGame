package com.mays.mtgboostergame.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserService implements UserDetailsService {
    @Autowired private UserRepository userRepository;

    public Optional<User> create(String username, String password) {
        return Optional.of(userRepository.save(new User(username, password)));
    }

    public Optional<User> get(Integer id) {
       return userRepository.findById(id);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findOneByUsername(username);
    }

    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name:" + username + "not found"));
        var password = user.getPassword();
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();
        return new org.springframework.security.core.userdetails.User(username, password, authorities);

    }
}
