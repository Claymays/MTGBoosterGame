package com.mays.mtgboostergame.Data;

import io.magicthegathering.javasdk.resource.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class MyCard {

    @Id
    private Integer multiverseId;
    private String name;
    private String manacost;
    private String types;
    private String rarity;
    private String expansion;
    @Column(length = 300)
    private String text;
    private String pngUri;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    public MyCard(Card card, Deck deck, String pngUri) {
        String allTypes = "";
        for (String type : card.getTypes()) {
            allTypes += type;
        }
        this.types = allTypes;
        this.expansion = card.getSet();
        this.manacost = card.getManaCost();
        this.multiverseId = card.getMultiverseid();
        this.rarity = card.getRarity();
        this.name = card.getName();
        this.text = card.getText();
        this.deck = deck;
    }

    public MyCard(MyCard card, Deck deck) {
        this.types = card.getTypes();
        this.expansion = card.getExpansion();
        this.manacost = card.getManacost();
        this.multiverseId = card.getMultiverseId();
        this.rarity = card.getRarity();
        this.name = card.getName();
        this.text = card.getText();
        this.deck = deck;
        this.pngUri = pngUri;
    }
}
