import * as constants from './shared.js';
import { $, get, set, setUser} from "./shared.js";

let content = $('#content');

window.onload = () => {
    displayUser();
    displayDecks();
}

let user;
let decks;

function displayUser() {
    user = JSON.parse(get('user'));

    const title = $('#userTitle');
    title.innerText = user.username;

    let newDeckButton = document.createElement('button');
    newDeckButton.textContent = 'Create Deck';
    newDeckButton.addEventListener('click', createDeck);
    content.append(newDeckButton);

    let deckContainer = document.createElement('div');
    deckContainer.id = 'deckContainer';
    content.append(deckContainer);
}

function displayDecks() {
    decks = user.decks || [];

    decks.forEach(deck => {
        loadDeck(deck);
    });
}

async function createDeck() {
    const newDeckName = prompt('New deck\'s name:') || 'default';
    const newDeckParams = {
        userId: user.id,
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
        user.decks.push(newDeck);
        setUser(user);
        loadDeck(newDeck);
    }

}

function loadDeck(deck) {
    let deckContainer = $('#deckContainer');

    let deckButton = document.createElement("button");
    deckButton.setAttribute('id', deck.id);
    deckButton.textContent = deck.deckName;

    deckButton.addEventListener('click', function() {
        set('activeDeck', deck.id);
        location.href='/deck';
    });

    deckContainer.append(deckButton);
}