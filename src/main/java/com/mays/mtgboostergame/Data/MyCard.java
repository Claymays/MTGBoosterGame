package com.mays.mtgboostergame.Data;

import io.magicthegathering.javasdk.resource.Card;

import javax.persistence.*;

@Entity
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

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    public MyCard() {}

    public MyCard(Card card, Deck deck) {
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
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getManacost() {
        return manacost;
    }

    public void setManacost(String manacost) {
        this.manacost = manacost;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getExpansion() {
        return expansion;
    }

    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getMultiverseId() {
        return multiverseId;
    }

    public void setMultiverseId(Integer multiverseId) {
        this.multiverseId = multiverseId;
    }
}
