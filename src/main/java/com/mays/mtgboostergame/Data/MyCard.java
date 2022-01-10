package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.magicthegathering.javasdk.resource.Card;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.web.client.RestTemplate;

import javax.persistence.*;
import java.lang.reflect.Array;
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
//    private String pngUri;

    @ManyToMany(mappedBy = "cardsInDeck")
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
    }

//    public MyCard(Card card, RestTemplate restTemplate) {
//        String allTypes = "";
//        for (String type : card.getTypes()) {
//            allTypes += type + ", ";
//        }
//        this.types = allTypes.substring(0, allTypes.length() - 2);
//        this.expansion = card.getSet();
//        this.manacost = card.getManaCost();
//        this.rarity = card.getRarity();
//        this.name = card.getName();
//        this.text = card.getText();
//        this.decks = new ArrayList<>();
//        this.pngUri = card
//    }


    public void setDeck(Deck deck) {
        this.decks.add(deck);
    }
}
