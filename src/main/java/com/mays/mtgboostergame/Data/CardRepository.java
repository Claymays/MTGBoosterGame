package com.mays.mtgboostergame.Data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends CrudRepository<MyCard, Integer> {
    Optional<MyCard> findOneByMultiverseId(Integer multiverseId);
    Boolean existsByMultiverseId(Integer multiverseId);
}

