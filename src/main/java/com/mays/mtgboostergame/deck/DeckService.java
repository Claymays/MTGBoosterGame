package com.mays.mtgboostergame.deck;

import com.mays.mtgboostergame.user.User;
import com.mays.mtgboostergame.user.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mays.mtgboostergame.deck.DeckController.DeckRequestBody;

@Data
@NoArgsConstructor
@Service
public class DeckService {
    private DeckRepository deckRepository;
    private UserService userService;

    @Autowired
    public DeckService(DeckRepository deckRepository, UserService userService) {
        this.deckRepository = deckRepository;
        this.userService = userService;
    }


    public Optional<Deck> create(DeckRequestBody newDeck) {
        Optional<User> user = userService.get(newDeck.getUserId());
        if (user.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(deckRepository.save(new Deck(user.get(), newDeck.getDeckName())));
        }
    }

    public Optional<Deck> get(Integer id) {
        return deckRepository.findById(id);
    }

    public Deck save(Deck deck) {
        return deckRepository.save(deck);
    }

    public void delete(Integer id) {
        deckRepository.deleteById(id);
    }
}
