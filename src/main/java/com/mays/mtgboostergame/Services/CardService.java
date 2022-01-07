package com.mays.mtgboostergame.Services;

import com.mays.mtgboostergame.Data.CardRepository;
import com.mays.mtgboostergame.Data.Deck;
import com.mays.mtgboostergame.Data.MyCard;
import io.magicthegathering.javasdk.api.CardAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.mays.mtgboostergame.Services.DeckService.*;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@Data
@NoArgsConstructor
@Service
@AllArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final RestTemplate restTemplate;
    private URI uri;

    @Autowired
    public CardService(CardRepository cardRepository, DeckService deckService, RestTemplate restTemplate) {
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.restTemplate = restTemplate;
    }

    

    public Optional<MyCard> addCarMyDeck(Integer deckId, Integer multiverseId, Integer quantity) {
        Deck deck = deckService.get(deckId);
        if (deck == null) {
            return Optional.empty();
        }

        Optional<MyCard> cardOpt = cardRepository.findById(multiverseId);
        if (cardOpt.isEmpty()) {
            cardOpt = Optional.of(cardRepository.save(new MyCard(CardAPI.getCard(multiverseId), restTemplate)));
        }


        for (int i = 0; i < quantity; i++) {
            deck.getCardsInDeck().add(cardOpt.get());
        }
        deckService.save(deck);

        return cardOpt;

    }

    public Optional<MyCard> getCard(Integer id) {
        return cardRepository.findById(id);
    }
}
