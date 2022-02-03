package com.mays.mtgboostergame.Services;

import com.mays.mtgboostergame.Data.CardRepository;
import com.mays.mtgboostergame.Data.Deck;
import com.mays.mtgboostergame.Data.MyCard;
import com.mays.mtgboostergame.Data.ScryFallCard;
import io.magicthegathering.javasdk.api.CardAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
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
    private String scryNameSearch;

    @Autowired
    public CardService(CardRepository cardRepository, DeckService deckService, RestTemplate restTemplate) {
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.restTemplate = restTemplate;
        this.scryNameSearch = "https://api.scryfall.com" + "/cards/named/?fuzzy=";
    }

    public Optional<MyCard> databaseEntry(MyCard card) {
        if (cardRepository.existsById(card.getId())) {
            log.info("card: name: {} Id: {} already present in database", card.getName(), card.getId());
            return Optional.empty();
        } else {
            return Optional.of(cardRepository.save(card));
        }
    }

    public Optional<MyCard> addCardToDeck(Integer deckId, String cardName, Integer quantity) {
        uri = URI.create(scryNameSearch + cardName);
        Optional<Deck> deck = deckService.get(deckId);
        if (deck.isEmpty()) {
            return Optional.empty();
        }

        Optional<MyCard> cardOpt = cardRepository.findOneByNameIgnoreCase(cardName);
        if (cardOpt.isEmpty()) {
            cardOpt = Optional.of(new MyCard(restTemplate.getForObject(uri, ScryFallCard.class)));
            if (cardOpt.isEmpty()) {
                return Optional.empty();
            }
            cardRepository.save(cardOpt.get());
        }

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
        Optional<MyCard> card = cardRepository.findOneByNameIgnoreCase(name);
        if (card.isEmpty()) {
            card = Optional.of(new MyCard(restTemplate.getForObject(scryNameSearch.concat(name), ScryFallCard.class)));
        }
        return card;
    }

    public void deleteAll() {
        cardRepository.deleteAll();
    }
}
