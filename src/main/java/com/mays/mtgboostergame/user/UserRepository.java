package com.mays.mtgboostergame.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findOneByUsername(String username);
    boolean existsByUsername(String username);

}
