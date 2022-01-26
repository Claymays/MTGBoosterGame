    'use strict'
var baseUrl = 'http://localhost:8080';

async function createUser() {
    var params = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    var userSearchUrl = baseUrl + '/api/user';

    const user = await fetch(userSearchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    }).then(response => {return response.json()})
    .catch(error => {
        console.log(error);
    });
    if (user != null) {
        localStorage.setItem(user.token, JSON.stringify(user));
    }
    location.href = '/userPage';
}

async function userAuth() {
    var params = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    var searchUrl = baseUrl + '/api/user/login';

    var response = await fetch(searchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    })
    .then(data => {return data.json()});
    setUser(response);
}

function setUser(user) {
    var decks = user.decks;
    localStorage.setItem(user.token, JSON.stringify(user));
    decks.forEach(deck => {
        localStorage.setItem(deck.id, JSON.stringify(deck));
    });
    location.href = baseUrl + '/userPage';
}

function loadUser() {
    const user = JSON.parse(localStorage.getItem('token'));
    const title = document.getElementById('userTitle');
    var container = document.getElementById('deckContainer');
    const decks = user.decks || '[]';

    title.innerHTML = user.username;

    decks.forEach(deck => {
        var block = document.createElement("button");
        block.setAttribute('id', deck.id);
        block.setAttribute('onclick', "localStorage.setItem('activeDeck', " + deck.id +"), location.href=\'/deck\'");
        block.innerHTML = deck.deckName;
        container.appendChild(block);
    });
}

async function createDeck() {
    const newDeckName = prompt('New deck\'s name:') || 'default';
    var user = JSON.parse(localStorage.getItem('token'));
    var decks = user.decks;
    const newDeckParams = {
        userId: user.id,
        deckName: newDeckName
    };
    const searchUrl = baseUrl + '/api/deck';
    const searchInit = {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
           },
           body: JSON.stringify(newDeckParams),
    };

    const newDeck = await fetch(searchUrl, searchInit)
    .then(response => {return response.json()});

    user.decks.push(newDeck);
    localStorage.setItem('token', JSON.stringify(user));

    var button = document.createElement('button');
    button.setAttribute('id', newDeck.id);
    button.setAttribute('onclick', "localStorage.setItem('activeDeck', " + newDeck.id +"), location.href=\'/deck\'");
    button.innerHTML = newDeck.deckName;
    var container = document.getElementById('deckContainer');
    container.appendChild(button);
}

function loadDeck() {
    const user = JSON.parse(localStorage.getItem('token'));
    var activeDeck = localStorage.getItem('activeDeck');
    var deck = JSON.parse(localStorage.getItem(activeDeck));
    var title = document.getElementById('title');
    var header = document.getElementById('deckHeader');
    const container = document.getElementById('deckContainer');

    title.innerHTML = deck.deckName;
    var deckTitle = document.createElement('span');
    deckTitle.innerHTML = deck.deckName;
    header.appendChild(deckTitle);

    var cards = deck.cardsInDeck || '[]';

    cards.forEach(card => {
        var a = document.createElement('a');
        a.setAttribute('href',"/card");
        var cardElement = document.createElement('img');
        cardElement.src = card.pngUri;
        a.appendChild(cardElement);
        container.appendChild(a);
    });

}

function deleteDeck() {
    var deckId = localStorage.getItem('activeDeck');
    var url = baseUrl + '/api/deck/' + deckId;
    fetch(url, {
        method: 'DELETE',
        });
    location.href = '/userPage';
}

async function cardSearch() {
    var cardSearchParams = '?name=' + document.getElementById('searchBar').value;
    var searchUrl = baseUrl + '/api/card/' + cardSearchParams;
    const card = await fetch(searchUrl)
    .then(response => {
        return response.json();
    });
    localStorage.setItem('card', JSON.stringify(card));
    location.href = '/card'
}
