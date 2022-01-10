package com.mays.mtgboostergame.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.mays.mtgboostergame.Data.ScryFallCard;
import com.mays.mtgboostergame.Data.MyCard;
import com.mays.mtgboostergame.Services.CardService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@NoArgsConstructor
@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {
    CardService cardService;
    URI uri;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
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
//            this.pngUri = card.getPngUri();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DTOCard> addToDeck(
              @PathVariable UUID cardId
            , @RequestParam Integer deckId
            , @RequestParam(required = false, defaultValue = "1") Integer quantity) {

        DTOCard card = new DTOCard(cardService.addCardToDeck(deckId, cardId, quantity).get());
        if (card == null) {
            return ResponseEntity.notFound().build();
        } else {
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(card);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOCard> getCard(@PathVariable UUID id) {
        DTOCard card = new DTOCard(cardService.getCard(id).get());

        if (card != null) {
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/massEntry")
    public void readFile()  throws IOException {
        try {
            Path path = Paths.get("C:\\Users\\Clayton\\Downloads\\oracle-cards.json");
            BufferedReader reader = Files.newBufferedReader(path);
            Gson gson = new Gson();
            // TODO: switch to jackson's parser.
            JsonStreamParser p = new JsonStreamParser(reader);

            JsonElement cardsElement = p.next();
            if (!cardsElement.isJsonArray()) {
                log.error("Read file failed on file: " + path.toString());
            } else {
                JsonArray cardsArray = cardsElement.getAsJsonArray();
                for (JsonElement cardElement : cardsArray) {
                    if (!cardElement.isJsonObject()) {
                        log.error("Element is not a Json object");
                    } else {
                        ScryFallCard scryFallCard = gson.fromJson(cardElement, ScryFallCard.class);
                        cardService.databaseEntry(new MyCard(scryFallCard));
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage() + "File not found");
        }
    }

    @GetMapping("name")
    public ResponseEntity<DTOCard> getByName(@RequestParam String name) {
        DTOCard card = new DTOCard(cardService.getCardByName(name).get());
        if (card == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(card);
        }
    }
}
