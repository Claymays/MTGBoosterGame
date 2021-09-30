package com.mays.mtgboostergame;

import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
public class RootController {
    JdbcTemplate database;
    @Autowired
    public RootController(JdbcTemplate jdbcTemplate) {
        this.database = jdbcTemplate;
    }
    @GetMapping("/card/{id}")
    public Card getCard(@PathVariable int id) {
        Card card = CardAPI.getCard(id);
        String types = Arrays.toString(card.getTypes());
        String sql = "insert into card (name, manacost, text, expansion, rarity, types, multiverseid) " +
                "values (" + String.format("'%s', '%s', '%s', '%s', '%s', '%s', '%s'", card.getName(), card.getManaCost(), card.getText(), card.getSet(), card.getRarity(), types, card.getMultiverseid()) + ")";
        int numRows = database.update(sql);
        log.info("Inserted {} card {}",  numRows, card.getName());
        return card;
    }
}
