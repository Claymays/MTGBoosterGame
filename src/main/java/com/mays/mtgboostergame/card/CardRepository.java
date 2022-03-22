package com.mays.mtgboostergame.card;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends CrudRepository<MyCard, UUID> {
    public Optional<MyCard> findOneByNameIgnoreCase(String name);

    public boolean existsByName(String name);
}

