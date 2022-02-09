package com.mays.mtgboostergame.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mays.mtgboostergame.deck.Deck;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class MyCard {

    @Id
    @Column(name = "card_id", unique = true)
    @Type(type = "uuid-char")
    private UUID id;
    @Column(name = "card_name")
    private String name;
    @Column(name = "mana_cost")
    private String manaCost;
    @Column(name = "type_line")
    private String typeLine;
    private String rarity;
    @Column(name = "set_name")
    private String setName;
    @Column(name = "oracle_text", length = 1000)
    private String oracleText;
    private String pngUri;

    @ManyToMany(mappedBy = "cardsInDeck")
    @JsonIgnore
    private List<Deck> decks;

    public MyCard(ScryFallCard card) {
        this.decks = new ArrayList<>();
        this.id = card.getId();
        this.name = card.getName();
        this.rarity = card.getRarity();
        this.manaCost = card.getManaCost();
        this.setName = card.getSetName();
        this.typeLine = card.getTypeLine();
        this.oracleText = card.getOracleText();
        this.pngUri = card.getPng();
    }

    public void setDeck(Deck deck) {
        this.decks.add(deck);
    }
}
