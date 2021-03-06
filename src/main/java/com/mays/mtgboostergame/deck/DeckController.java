package com.mays.mtgboostergame.deck;

import com.mays.mtgboostergame.card.CardController;
import com.mays.mtgboostergame.card.MyCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mays.mtgboostergame.card.CardController.*;
import static com.mays.mtgboostergame.card.CardController.DTOCard;

@NoArgsConstructor
@RestController
@RequestMapping(value = "/api/deck")
@AllArgsConstructor
@CrossOrigin
public class DeckController {

    private DeckService deckService;
    URI uri;

    @Autowired
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @Data
    @NoArgsConstructor
    public static class DTODeck {

        private Integer id;
        private String deckName;
        private Integer userId;
        private List<DTOCard> cardsInDeck;

        public DTODeck(Deck deck) {
            this.id = deck.getId();
            this.deckName = deck.getDeckName();
            this.userId = deck.getUser().getId();
            this.cardsInDeck = deck.getCardsInDeck().stream()
                    .map(DTOCard::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class DeckRequestBody {
        String deckName;
        String deckContent;
        public DeckRequestBody(String deckName, String deckContent) {

            if (Objects.equals(deckName, "")) {
                this.deckName = SecurityContextHolder.getContext().getAuthentication().getName() + "'s new deck";
            } else {
                this.deckName = deckName;
            }

            if (deckContent == null) {
                this.deckContent = "";
            } else {
                this.deckContent = deckContent;
            }
        }
    }

    @PostMapping()
    public ResponseEntity<DTODeck> createDeck(@RequestBody DeckRequestBody newDeck) {
        Optional<Deck> deck = deckService.create(newDeck);
        if (deck.isPresent()) {
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(new DTODeck(deck.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTODeck> getDeck(@PathVariable Integer id) {
        Optional<Deck> deck = deckService.get(id);

        return deck.map(value -> ResponseEntity.ok(new DTODeck(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/update")
    public ResponseEntity<DTODeck> updateCardsInDeck(@RequestBody CardRequestBody card) {
        System.out.println(card);
        Optional<Deck> deck = deckService.update(card);
        if (deck.isPresent()) {
            Deck newDeck = deck.get();
            return ResponseEntity.ok(new DTODeck(newDeck));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        deckService.delete(id);
        Optional<Deck> emptyDeck = deckService.get(id);
        if (emptyDeck.isEmpty()) {
            return ResponseEntity.ok("delete successful");
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
