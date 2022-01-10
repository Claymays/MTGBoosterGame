package com.mays.mtgboostergame.Services;

import com.mays.mtgboostergame.Data.CardRepository;
import com.mays.mtgboostergame.Data.Deck;
import com.mays.mtgboostergame.Data.MyCard;
import io.magicthegathering.javasdk.api.CardAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.mays.mtgboostergame.Services.DeckService.*;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@Data
@NoArgsConstructor
@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    private CardRepository cardRepository;
    private DeckService deckService;
    private RestTemplate restTemplate;
    private URI uri;

    @Autowired
    public CardService(CardRepository cardRepository, DeckService deckService, RestTemplate restTemplate) {
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.restTemplate = restTemplate;
    }

    public Optional<MyCard> databaseEntry(MyCard card) {
        if (cardRepository.existsById(card.getId())) {
            log.debug("card: " + card.getId() + " already present in database");
            return Optional.empty();
        } else {
            return Optional.of(cardRepository.save(card));
        }
    }

    public Optional<MyCard> addCardToDeck(Integer deckId, UUID cardId, Integer quantity) {
        Optional<Deck> deck = deckService.get(deckId);
        if (deck == null) {
            return Optional.empty();
        }

        Optional<MyCard> cardOpt = cardRepository.findById(cardId);


        for (int i = 0; i < quantity; i++) {
            deck.get().getCardsInDeck().add(cardOpt.get());
        }
        deckService.save(deck.get());

        return cardOpt;

    }

    public Optional<MyCard> getCard(UUID id) {
        return cardRepository.findById(id);
    }

    public Optional<MyCard> getCardByName(String name) {
        return cardRepository.findOneByName(name);
    }
}
