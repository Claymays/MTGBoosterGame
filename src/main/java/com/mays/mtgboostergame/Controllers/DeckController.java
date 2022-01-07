package com.mays.mtgboostergame.Controllers;

import com.mays.mtgboostergame.Data.Deck;
import com.mays.mtgboostergame.Services.CardService;
import com.mays.mtgboostergame.Services.DeckService;
import com.mays.mtgboostergame.Services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mays.mtgboostergame.Services.DeckService.DTODeck;

@NoArgsConstructor
@RestController
@RequestMapping("/deck")
@AllArgsConstructor
public class DeckController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DeckService deckService;
    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;
    URI uri;

    @Data
    @NoArgsConstructor
    public static class DTODeck {

        private Integer id;
        private String deckName;
        private Integer userId;
        private List<CardService.DTOCard> cardsInDeck;

        public DTODeck(Deck deck) {
            this.id = deck.getId();
            this.deckName = deck.getDeckName();
            this.userId = deck.getUser().getId();
            this.cardsInDeck = deck.getCardsInDeck().stream()
                    .map(CardService.DTOCard::new)
                    .collect(Collectors.toList());
        }
    }


    @PostMapping
    public ResponseEntity<DTODeck> createDeck(@RequestParam(value = "deck_name") String deckName, @RequestParam(value = "user_id") Integer userID) {
        DTODeck deck = deckService.create(deckName, userID);
        if (deck != null) {
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(deck);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTODeck> getDeck(@PathVariable Integer id) {
        DTODeck deck = deckService.get(id);

        if (deck != null) {
            return ResponseEntity.ok(deck);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
