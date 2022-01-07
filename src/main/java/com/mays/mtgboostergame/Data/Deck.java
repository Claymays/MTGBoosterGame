package com.mays.mtgboostergame.Data;

import com.mays.mtgboostergame.Services.DeckService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.mays.mtgboostergame.Services.DeckService.*;

@Entity
@Table(name="DECK")
@Data
@NoArgsConstructor
public class Deck {

    @Id
    @Column(name = "deck_id")
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private String deckName;

    @ManyToMany
    @JoinTable(name = "cards_in_decks",
                joinColumns =  @JoinColumn(name = "deck_id", referencedColumnName = "deck_id"),
                inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "card_id"))
    private List<MyCard> cardsInDeck;

    public Deck(User user, String deckName) {
        this.user = user;
        this.deckName = deckName;
        this.cardsInDeck = new ArrayList<>();
    }
}
