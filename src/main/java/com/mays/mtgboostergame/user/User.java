package com.mays.mtgboostergame.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mays.mtgboostergame.deck.Deck;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="USER")
@Data
@RequiredArgsConstructor
@EntityListeners(AuditListener.class)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;

    @CreatedDate
    private Instant created;
    @LastModifiedDate
    private Instant lastModified;

    @OneToMany(mappedBy = "user")
    private List<Deck> decks;

    @OneToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.created = Instant.now();
        this.lastModified = Instant.now();
        this.decks = new ArrayList<>();
    }
}
