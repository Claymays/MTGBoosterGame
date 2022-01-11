package com.mays.mtgboostergame.Controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.mays.mtgboostergame.Data.BulkData;
import com.mays.mtgboostergame.Data.MyCard;
import com.mays.mtgboostergame.Data.ScryFallCard;
import com.mays.mtgboostergame.Services.CardService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@NoArgsConstructor
@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {
    RestTemplate restTemplate;
    CardService cardService;
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
    URI uri;

    @Autowired
    public CardController(CardService cardService, RestTemplateBuilder builder) {
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
//            this.pngUri = card.getPngUri();
        }
    }

    @Data
    @NoArgsConstructor
    public static class JsonWrapper {
       JsonToken startToken = JsonToken.START_ARRAY;
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


    // TODO: switch to jackson's parser.
//    @PostMapping("/massEntry")
//    public ResponseEntity fetchBulkData() throws IOException {
//        JsonParser parser = null;
//        try {
//            BulkData source = restTemplate.getForEntity("https://api.scryfall.com/bulk-data", BulkData.class).getBody();
//            Path fileSource = Paths.get("https://c2.scryfall.com/file/scryfall-bulk/oracle-cards/oracle-cards-20220110100346.json");
//            BufferedReader reader = Files.newBufferedReader(fileSource);
//            JsonFactory fac = new JsonFactory();
//            parser = fac.createParser(reader);
//
//            parser.nextValue();
//            parser.nextValue();
//            parser.nextValue();
//
//            while(parser.nextValue() != JsonToken.END_ARRAY) {
//                ScryFallCard card = new ScryFallCard();
//                while (parser.nextToken() != JsonToken.END_OBJECT) {
//                    String fieldName = parser.getCurrentName();
//                    parser.nextToken();
//
//                    if (fieldName.equals("id")) {
//                        card.setId(parser.getText());
//                    }
//                }
//            }
//
//
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



    @PostMapping("/massEntry")
    public void readFile() throws URISyntaxException {

//            Path fileSource = Paths.get(source.getDataLocation());
//            BufferedReader reader = Files.newBufferedReader(fileSource);
//            JsonStreamParser p = new JsonStreamParser(reader);

        BulkData source = restTemplate.getForEntity("https://api.scryfall.com/bulk-data", BulkData.class).getBody();
        Gson gson = new Gson();
        JsonArray cardsElement = restTemplate.getForObject(
                source.getDataLocation()
              , JsonArray.class
        );
        if (!cardsElement.isJsonArray()) {
            log.error("Read file failed on location: " + source.getDataLocation());
        } else {
            JsonArray cardsArray = cardsElement.getAsJsonArray();
            for (JsonElement cardElement : cardsArray) {
                if (!cardElement.isJsonObject()) {
                    log.error("Element is not a Json object");
                } else {
//                        ScryFallCard card = new ObjectMapper()
//                                .readerFor(ScryFallCard.class)
//                                .readValue(cardElement);
                    ScryFallCard scryFallCard = gson.fromJson(cardElement, ScryFallCard.class);
                    cardService.databaseEntry(new MyCard(scryFallCard));
                }
            }
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
