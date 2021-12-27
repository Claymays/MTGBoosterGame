package com.mays.mtgboostergame.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="DECK")
public class Deck {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private String deckName;

    @OneToMany(mappedBy = "deck")
    private List<MyCard> cardsInDeck;

    public Deck() {}

    public Deck(User user, String deckName) {
        this.user = user;
        this.deckName = deckName;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
//    private String userForeignKey;
//    private String deckForeignKey;
//    private ArrayList<Integer> listOfCards;
}
