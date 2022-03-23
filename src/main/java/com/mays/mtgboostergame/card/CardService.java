package com.mays.mtgboostergame.card;

import com.mays.mtgboostergame.deck.DeckService;
import com.mays.mtgboostergame.deck.Deck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.mays.mtgboostergame.card.CardController.*;

@Data
@NoArgsConstructor
@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    private CardRepository cardRepository;
    private DeckService deckService;
    private RestTemplate restTemplate;
    private String scryNameSearch;

    @Autowired
    public CardService(CardRepository cardRepository, DeckService deckService, RestTemplate restTemplate) {
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.restTemplate = restTemplate;
        this.scryNameSearch = "https://api.scryfall.com" + "/cards/named/?fuzzy=";
    }

    public void databaseEntry(MyCard card) {
        if (cardRepository.existsById(card.getId())) {
            log.info("card: name: {} Id: {} already present in database", card.getName(), card.getId());
        } else {
            cardRepository.save(card);
        }
    }

    public Optional<MyCard> addCardToDeck(CardRequestBody card) {
        Optional<Deck> deck = deckService.get(card.deckId);
        if (deck.isEmpty()) {
            return Optional.empty();
        }

        Optional<MyCard> cardOpt = cardRepository.findOneByNameIgnoreCase(card.cardName);
        if (cardOpt.isEmpty()) {
            cardOpt = Optional.of(new MyCard(restTemplate.getForObject(scryNameSearch + card.cardName, ScryFallCard.class)));
            if (cardOpt.isEmpty()) {
                return Optional.empty();
            }
            cardRepository.save(cardOpt.get());
        }

        for (int i = 0; i < card.quantity; i++) {
            deck.get().getCardsInDeck().add(cardOpt.get());
        }

        deckService.save(deck.get());
        Optional<MyCard> savedCard = Optional.of(cardRepository.save(cardOpt.get()));

        return savedCard;

    }

    public Optional<MyCard> getCard(UUID id) {
        return cardRepository.findById(id);
    }

    public Optional<MyCard> getCardByName(String name) {
        Optional<MyCard> card = cardRepository.findOneByNameIgnoreCase(name);

        if (card.isEmpty()) {
            ScryFallCard checkedCard = restTemplate.getForObject(scryNameSearch.concat(name), ScryFallCard.class);
            if (checkedCard == null) {
                return Optional.empty();
            }

            card = Optional.of(new MyCard(checkedCard));

            if (!cardRepository.existsByName(card.get().getName())) {
                 card = Optional.of(cardRepository.save(card.get()));
            }

        }
        return card;
    }

    public void deleteAll() {
        cardRepository.deleteAll();
    }
}
