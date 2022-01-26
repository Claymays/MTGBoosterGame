package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="USER")
@Data
@NoArgsConstructor
public class User {

    @Id
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
