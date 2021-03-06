package com.mays.mtgboostergame.deck;

import com.mays.mtgboostergame.card.CardController;
import com.mays.mtgboostergame.card.CardService;
import com.mays.mtgboostergame.card.MyCard;
import com.mays.mtgboostergame.user.User;
import com.mays.mtgboostergame.user.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.mays.mtgboostergame.deck.DeckController.DeckRequestBody;

@Data
@NoArgsConstructor
@Service
public class DeckService {
    private DeckRepository deckRepository;
    private CardService cardService;
    private UserService userService;

    @Autowired
    public DeckService(DeckRepository deckRepository, UserService userService, @Lazy CardService cardService) {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.cardService = cardService;
    }


    public Optional<Deck> create(DeckRequestBody newDeck) {
        Optional<User> user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<MyCard> list = new ArrayList<>();

        if (user.isEmpty()) {
            return Optional.empty();
        } else {
            Deck deck = deckRepository.save(new Deck(user.get(), newDeck.deckName, list));
            Scanner scan = new Scanner(newDeck.deckContent);

            while(scan.hasNextLine()) {

                int quantity = 1;
                String uncutCardName;

                if (scan.hasNextInt()) {
                    quantity = scan.nextInt();
                }
                uncutCardName = scan.nextLine();
                String cardName = uncutCardName.trim();
                if (cardName.matches("Deck") || cardName.matches("Sideboard") || cardName.equals("") || cardName.matches("Commander")) {
                    continue;
                }

                System.out.println(quantity + " " + cardName);

                cardService.addCardToDeck(new CardController.CardRequestBody(cardName, deck.getId(), quantity));
            }

            deckRepository.save(deck);

            return Optional.of(deck);
        }
    }

    public Optional<Deck> get(Integer id) {
        return deckRepository.findById(id);
    }

    public Optional<Deck> update(CardController.CardRequestBody cardRequestBody) {
        Optional<Deck> optDeck = deckRepository.findById(cardRequestBody.deckId);
        Deck deck;
        MyCard card;
        Deck newDeck;
        int listedEntries = 0;

        if (optDeck.isPresent()) {
            deck = optDeck.get();
        } else {
            return Optional.empty();
        }
        List<MyCard> oldList = deck.getCardsInDeck();

        Optional<MyCard> optCard = cardService.getCardByName(cardRequestBody.cardName);


        if (optCard.isPresent()) {
            card = optCard.get();

            for (MyCard listedCard : oldList) {
                if (listedCard.getName().equals(card.getName())) {
                    listedEntries++;
                    System.out.print(listedEntries + " ");
                }
            }

            if (listedEntries > cardRequestBody.quantity) {
                System.out.println("removing: " + card.getName() + " from deck: " + deck.getDeckName() + " " + ( listedEntries - cardRequestBody.quantity ) + " times");
                for (int i = listedEntries; i > cardRequestBody.quantity; i--) {
                    oldList.remove(card);
                    System.out.print(oldList);
                }
            } else if (listedEntries < cardRequestBody.quantity) {
                System.out.println("adding: " + card.getName() + " to deck: " + deck.getDeckName() + " " +  (cardRequestBody.quantity - listedEntries) + " times");
                for (int i = listedEntries; i < cardRequestBody.quantity; i++) {
                    oldList.add(card);
                }
            }

            deck.setCardsInDeck(oldList);
            newDeck = deckRepository.save(deck);
            return Optional.of(newDeck);

        } else {
            return Optional.empty();
        }
    }

    public Deck save(Deck deck) {
        return deckRepository.save(deck);
    }

    public void delete(Integer id) {
        deckRepository.deleteById(id);
    }

//    public List<MyCard> formatCards(String file) {
//        Scanner scan = new Scanner(file);
//        List<MyCard> list = new ArrayList<>();
//
//        while(scan.hasNextLine()) {
//            Deck deck = deckRepository.save()
//            int quantity = 1;
//            String name;
//
//            if (scan.hasNextInt()) {
//                quantity = scan.nextInt();
//            }
//            name = scan.nextLine();
//            if (name.matches("Deck") || name.matches("Sideboard") || name.equals("")) {
//                continue;
//            }
//
//            System.out.println(quantity + name);
//            Optional<MyCard> optCard = cardService.getCardByName(name);
//            if (optCard.isPresent()) {
//                MyCard card = optCard.get();
//                for (int i = 0; i < quantity; i++) {
//                    list.add(card);
//                }
//            }
//        }
//        return list;
//    }
}
