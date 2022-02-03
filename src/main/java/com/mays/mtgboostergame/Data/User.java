package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="USER")
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Deck> decks;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.decks = new ArrayList<>();
    }
}
