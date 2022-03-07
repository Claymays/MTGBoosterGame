import * as constants from './shared.js';
import {$, logout, get, set} from './shared.js';

let content = $('#content');

window.onload = async () => {
    await displayUser();
    displayDecks();
}

let user;
let decks;



async function displayUser() {
    user = await fetch(constants.paths.users, {
        method: 'POST',
        headers: {
            'Authorization': 'bearer' + get('token')
        },
    })
        .then(response => {return response.json()})
        .catch(() => {logout()})

    const title = $('#userTitle');
    title.innerText = user.username;

    let createDeckButton = document.createElement('button');
    createDeckButton.textContent = 'Create Deck';
    createDeckButton.addEventListener('click', createDeck);
    content.append(createDeckButton);

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

function createDeck() {
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

    fetch(constants.paths.decks, searchInit)
        .then(response => {
            return response.json()
        })
        .then(deck => {
            user.decks.push(deck);
            loadDeck(deck);
        })
        .catch(error => {
            alert('this happened: ' + error);
        }) ;
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