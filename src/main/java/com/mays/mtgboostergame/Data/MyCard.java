package com.mays.mtgboostergame.Data;

import io.magicthegathering.javasdk.resource.Card;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class MyCard {

    @Id
    @Column(name = "card_id")
    @GeneratedValue
    private Integer id;
    private Integer multiverseId;
    private String name;
    private String manacost;
    private String types;
    private String rarity;
    private String expansion;
    @Column(length = 300)
    private String text;
    private String pngUri;

    @ManyToMany(mappedBy = "cardsInDeck")
    private List<Deck> decks;

    public MyCard(Card card, RestTemplate restTemplate) {
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
        this.decks = new ArrayList<>();
        this.pngUri = restTemplate.getForObject(
                "https://api.scryfall.com/cards/multiverse/" + card.getMultiverseid(), MagicIMG.class
        ).getImageUris().getPng();
    }

    public void setDeck(Deck deck) {
        this.decks.add(deck);
    }
}
