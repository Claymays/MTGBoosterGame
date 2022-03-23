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
        Optional<User> user = userService.get(newDeck.getUserId());
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
                if (cardName.matches("Deck") || cardName.matches("Sideboard") || cardName.equals("")) {
                    continue;
                }

                System.out.println(quantity + cardName);

                cardService.addCardToDeck(new CardController.CardRequestBody(cardName, deck.getId(), quantity));
            }

            deckRepository.save(deck);

            return Optional.of(deck);
        }
    }

    public Optional<Deck> get(Integer id) {
        return deckRepository.findById(id);
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
