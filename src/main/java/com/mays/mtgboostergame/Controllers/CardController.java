package com.mays.mtgboostergame.Controllers;

import com.mays.mtgboostergame.Data.MyCard;
import com.mays.mtgboostergame.Services.CardService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.mays.mtgboostergame.Services.CardService.DTOCard;

@NoArgsConstructor
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired CardService cardService;
    URI uri;

    @Data
    @NoArgsConstructor
    public static class DTOCard {
        private Integer id;
        private Integer multiverseId;
        private String name;
        private String manacost;
        private String types;
        private String rarity;
        private String expansion;
        private String text;
        private String pngUri;

        public DTOCard(MyCard card) {
            this.id = card.getId();
            this.multiverseId = card.getMultiverseId();
            this.name = card.getName();
            this.manacost = card.getManacost();
            this.types = card.getTypes();
            this.rarity = card.getRarity();
            this.expansion = card.getExpansion();
            this.text = card.getText();
            this.pngUri = card.getPngUri();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DTOCard> addToDeck(
              @PathVariable Integer id
            , @RequestParam Integer multiverseID
            , @RequestParam(required = false, defaultValue = "1") Integer quantity) {

        DTOCard card = cardService.addCardToDeck(id, multiverseID, quantity);
        if (card == null) {
            return ResponseEntity.notFound().build();
        } else {
            uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(uri).body(card);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOCard> getCard(@PathVariable Integer id) {
        DTOCard card = cardService.getCard(id);

        if (card != null) {
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
