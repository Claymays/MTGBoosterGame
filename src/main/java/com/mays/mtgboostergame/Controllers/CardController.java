package com.mays.mtgboostergame.Controllers;

import com.mays.mtgboostergame.Data.BulkData;
import com.mays.mtgboostergame.Data.MyCard;
import com.mays.mtgboostergame.Data.ScryFallCard;
import com.mays.mtgboostergame.Services.CardService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@RestController
@RequestMapping("/api/card")
@Slf4j
public class CardController {
    RestTemplate restTemplate;
    CardService cardService;
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
    URI uri;

    @Autowired
    public CardController(CardService cardService) {
        this.httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build()
        );
        this.cardService = cardService;
        this.restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @Data
    @NoArgsConstructor
    public static class DTOCard {
        private UUID id;
        private String name;
        private String manacost;
        private String types;
        private String rarity;
        private String expansion;
        private String text;
        private String pngUri;

        public DTOCard(MyCard card) {
            this.id = card.getId();
            this.name = card.getName();
            this.manacost = card.getManaCost();
            this.types = card.getTypeLine();
            this.rarity = card.getRarity();
            this.expansion = card.getSetName();
            this.text = card.getOracleText();
            this.pngUri = card.getPngUri();
        }
    }

    @PostMapping("/")
    public ResponseEntity<DTOCard> addToDeck(
              @RequestParam String cardName
            , @RequestParam Integer deckId
            , @RequestParam(required = false, defaultValue = "1") Integer quantity) {

        DTOCard card = new DTOCard(cardService.addCardToDeck(deckId, cardName, quantity).get());
        if (card == null) {
            return ResponseEntity.notFound().build();
        } else {
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(card);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOCard> getCard(@PathVariable UUID id) {
        Optional<MyCard> optMyCard = cardService.getCard(id);
        if (optMyCard.isEmpty()) {
            log.info("Card not found with Id: {}", id);
            return ResponseEntity.notFound().build();
        }
        MyCard unpackedCard = optMyCard.get();
        DTOCard card = new DTOCard(unpackedCard);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/")
    public ResponseEntity<DTOCard> getByName(@RequestParam String name) {
        DTOCard card = new DTOCard(cardService.getCardByName(name).get());
        if (card == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(card);
        }
    }

    @PostMapping("/massEntry")
    public void readFile() {

        BulkData source = restTemplate.getForEntity("https://api.scryfall.com/bulk-data", BulkData.class).getBody();
        ScryFallCard[] cards = restTemplate.getForObject(
                source.getDataLocation()
                , ScryFallCard[].class
        );

        if (cards == null) {
            log.error("Array not received");
        } else {
            for (ScryFallCard card : cards) {
                cardService.databaseEntry(new MyCard(card));
            }
        }
    }
}
