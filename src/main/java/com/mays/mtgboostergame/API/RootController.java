package com.mays.mtgboostergame.API;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mays.mtgboostergame.Data.*;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@RestController
public class RootController {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public RootController(CardRepository cardRepository
            , UserRepository userRepository
            , DeckRepository deckRepository) {

        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
    }


    @PostMapping("/register")
    public Integer createUser(@RequestParam(value = "name") String username, @RequestParam(value = "pass") String password) {
        User newUser = new User(username, password);
        User user = userRepository.save(newUser);
        return user.getId();
    }

    @PostMapping("/createDeck")
    public @ResponseBody String createDeck(@RequestParam(value = "deckName") String deckName, @RequestParam(value = "user") Integer userID) {
        if (userRepository.existsById(userID)) {
            Optional<User> user = userRepository.findById(userID);
            Deck deck = deckRepository.save(new Deck(user.get(), deckName));
            return deck.getId().toString();
        } else {
            return userID + "User not found";
        }
    }

    @PostMapping("/deckLookup")
    public @ResponseBody String deckLookup(@RequestParam(value = "deckID") Integer deckID,@RequestParam(value = "deckName") String deckName) {
        if (deckRepository.existsById(deckID)) {
            return deckRepository.findById(deckID).get().toString();
        } else {
            for (Deck deck : deckRepository.findAll()) {
                if (deck.getDeckName().toString().equalsIgnoreCase(deckName)) {
                    return "Deck name:" + deck.getDeckName().toString() + ", deck id:" + deck.getId();
                }
            }
        }
        return "Deck not found";
    }

    @PostMapping("/addCardToDeck")
    public void addToDeck(
              @RequestParam(value = "deckID") Integer id
            , @RequestParam(value = "multiverseID") Integer multiverseID
            , @RequestParam(value = "quantity") Integer quantity) {

        Optional<Deck> activeDeck = deckRepository.findById(id);

        MagicIMG newCardsPng = restTemplate.getForObject(
                "https://api.scryfall.com/cards/multiverse/" + multiverseID, MagicIMG.class
        );

        Optional<MyCard> cardToAdd = cardRepository.existsById(multiverseID)
                ? cardRepository.findById(multiverseID)
                : Optional.of(new MyCard(CardAPI.getCard(multiverseID), activeDeck.get()));

        for (int i = quantity; i > 0; i--) {
            cardRepository.save(cardToAdd.get());
        }

    }

    @GetMapping("/card/{id}")
    public @ResponseBody Optional<MyCard> getCard(@PathVariable Integer id) {
        return cardRepository.findById(id);
    }

    @PostMapping("/card/{id}")
    public @ResponseBody String addCard (@PathVariable Integer id, @RequestParam Integer deckID) {
        Deck activeDeck = deckRepository.findById(deckID).get();
        MyCard cardToAdd = new MyCard(CardAPI.getCard(id), activeDeck);
        cardRepository.save(cardToAdd);
        return "saved";
    }
    @GetMapping("/test")
    public String test() {
        MagicIMG newCardsPng = restTemplate.getForObject(
                "https://api.scryfall.com/cards/multiverse/409574", MagicIMG.class
        );
        return newCardsPng.getPng();
    }

}
