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

    public Optional<MyCard> addCardToDeck(Integer deckId, String cardName, Integer quantity) {
        Optional<Deck> deck = deckService.get(deckId);
        if (deck.isEmpty()) {
            return Optional.empty();
        }

        Optional<MyCard> cardOpt = cardRepository.findOneByNameIgnoreCase(cardName);
        if (cardOpt.isEmpty()) {
            cardOpt = Optional.of(new MyCard(restTemplate.getForObject(scryNameSearch + cardName, ScryFallCard.class)));
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
