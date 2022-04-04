package com.mays.mtgboostergame.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mays.mtgboostergame.deck.Deck;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="USER")
@Getter
@Setter
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<Role> roles;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.created = Instant.now();
        this.lastModified = Instant.now();
        this.decks = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.roles.add(new Role(role));
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
