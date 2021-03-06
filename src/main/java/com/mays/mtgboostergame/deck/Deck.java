package com.mays.mtgboostergame.deck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mays.mtgboostergame.card.MyCard;
import com.mays.mtgboostergame.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="DECKS")
@Data
@NoArgsConstructor
public class Deck {

    @Id
    @Column(name = "deck_id")
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinTable(name = "deck_table",
    joinColumns = @JoinColumn(name = "deck_id", referencedColumnName = "deck_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    @JsonIgnore
    private User user;

    private String deckName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cards_in_decks",
                joinColumns =  @JoinColumn(name = "deck_id", referencedColumnName = "deck_id"),
                inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "card_id"))
    private List<MyCard> cardsInDeck;

    public Deck(User user, String deckName, List<MyCard> cardsInDeck) {
        this.user = user;
        this.deckName = deckName;
        this.cardsInDeck = cardsInDeck;
    }

    @Override
    public String toString() {
        return "Deck: " + deckName + "Deck_id: " + id +
                "User: " + user.getUsername() + "Cards: " + cardsInDeck.stream().map(MyCard::getName).collect(Collectors.joining(",", "[", "]"));
    }
}
