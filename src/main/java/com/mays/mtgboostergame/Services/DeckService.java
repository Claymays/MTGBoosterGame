package com.mays.mtgboostergame.Services;

import com.mays.mtgboostergame.Data.Deck;
import com.mays.mtgboostergame.Data.DeckRepository;
import com.mays.mtgboostergame.Data.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@NoArgsConstructor
@Service
@AllArgsConstructor
public class DeckService {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserService userService;



    public Deck create(String deckName, Integer userID) {
        Optional<User> user = userService.getUser(userID);
    }

    public Deck get(Integer id) {
        if (deckRepository.existsById(id)) {
            return deckRepository.findById(id).get();
        } else {
            return null;
        }
    }

    public Deck save(Deck deck) {
        return deckRepository.save(deck);
    }
}
