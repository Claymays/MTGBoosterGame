import * as constants from './shared';
import { $, get, set, setUser} from "./shared";

let content = $('#content');

window.onload = () => {
    displayUser();
    displayDecks();
}

function displayUser() {
    constants.user = JSON.parse(get('user'));

    const title = $('userTitle');
    title.innerText = constants.user.username;

    let newDeckButton = document.createElement('button');
    newDeckButton.textContent = 'Create Deck';
    newDeckButton.addEventListener('click', createDeck);
    content.append(newDeckButton);

    let deckContainer = document.createElement('div');
    deckContainer.id = 'deckContainer';
    content.append(deckContainer);
}

function displayDecks() {
    constants.decks = constants.user.decks || [];

    constants.decks.forEach(deck => {
        loadDeck(deck);
    });
}

async function createDeck() {
    const newDeckName = prompt('New deck\'s name:') || 'default';
    const newDeckParams = {
        userId: constants.user.id,
        deckName: newDeckName
    };
    const searchInit = {
        method: 'POST',
        headers: {
            'Authorization': 'bearer' + get('token'),
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(newDeckParams),
    };

    const newDeck = await fetch(constants.paths.decks, searchInit)
        .then(response => {
            return response.json()
        });

    if (newDeck != null ) {
        constants.user.decks.push(newDeck);
        setUser(constants.user);
        loadDeck(newDeck);
    }

}

function loadDeck(deck) {
    let deckContainer = $('#deckContainer');

    let deckBlock = document.createElement("button");
    deckBlock.setAttribute('id', deck.id);
    deckBlock.innerText = deck.deckName;

    deckBlock.addEventListener('click', function() {
        set('activeDeck', deck.id);
        location.href='/deck';
    });

    deckContainer.append(deckBlock);
}