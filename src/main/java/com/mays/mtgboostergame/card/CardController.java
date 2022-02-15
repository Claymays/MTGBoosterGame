package com.mays.mtgboostergame.card;

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
        private String typeLine;
        private String rarity;
        private String expansion;
        private String text;
        private String pngUri;

        public DTOCard(MyCard card) {
            this.id = card.getId();
            this.name = card.getName();
            this.manacost = card.getManaCost();
            this.typeLine = card.getTypeLine();
            this.rarity = card.getRarity();
            this.expansion = card.getSetName();
            this.text = card.getOracleText();
            this.pngUri = card.getPngUri();
        }
    }

    @PostMapping
    public ResponseEntity<DTOCard> addToDeck(
              @RequestParam String cardName
            , @RequestParam Integer deckId
            , @RequestParam(required = false, defaultValue = "1") Integer quantity) {

        Optional<MyCard> optionalMyCard = cardService.addCardToDeck(deckId, cardName, quantity);
        if (optionalMyCard.isPresent()) {
            DTOCard card = new DTOCard(optionalMyCard.get());
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(card);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOCard> getById(@PathVariable UUID id) {
        Optional<MyCard> optMyCard = cardService.getCard(id);
        if (optMyCard.isEmpty()) {
            log.info("Card not found with Id: {}", id);
            return ResponseEntity.notFound().build();
        }
        MyCard unpackedCard = optMyCard.get();
        DTOCard card = new DTOCard(unpackedCard);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<DTOCard> getByName(@RequestParam String name) {
        Optional<MyCard> optionalCard = cardService.getCardByName(name);
        if (optionalCard.isPresent()) {
            DTOCard card = new DTOCard(optionalCard.get());
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.notFound().build();
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
