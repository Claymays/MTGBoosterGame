    'use strict'
var baseUrl = 'http://localhost:8080';
var password;
var username;

function createUser() {
    var params = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    var userSearchUrl = baseUrl + '/api/user';

    fetch(userSearchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    }).then(response => response.json())
    .then(data => {

        }).catch(error => {
        console.log(error);
    });

}

function userAuth() {
    var params = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    var searchUrl = baseUrl + '/api/user/login';

    fetch(searchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    })
    .then(response => { return response.json();
    }).then(user => {
        loadUser(user);
    }).then(function() {
        loadUser();
    });
}
function getCardByName() {
    var cardSearchParams = '?name=' + document.getElementById('searchBar').value;
    var searchUrl = baseUrl + '/api/card/' + cardSearchParams;
    fetch(searchUrl)
    .then(response => {
        return response.json();
    }).then(data => {
        var card = data;
        const img = document.createElement("img");
        img.src = card.pngUri;
        img.name = card.name;
        const src = document.getElementById("test");
        src.appendChild(img);
    });
}

function createDeck() {
    const newDeckName = prompt('New deck\'s name:');
    const newDeckParams = {
        userId: localStorage.getItem('userId'),
        deckName: newDeckName
    };
    if (newDeckName != null) {
        const searchUrl = baseUrl + '/api/deck';
        fetch(searchUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newDeckParams),
        })
        .then(response => {return response.json()})
        .then(data => {
            var decks = JSON.parse(localStorage.getItem('decks'));
            decks.push(data);
            localStorage.setItem('decks', JSON.stringify(decks));
        })
    }
}

function setUser(user) {
    localStorage.setItem('username', user.username);
    localStorage.setItem('userId', user.id);
    user.decks.forEach(deck => {
        localStorage.setItem(deck.id, JSON.stringify(deck));
    })
    localStorage.setItem('decks', JSON.stringify(user.decks));
    location.href = baseUrl + '/userPage';
}

function loadUser() {
    var title = document.getElementById('userTitle');
    title.innerHTML = localStorage.getItem('username');
    var container = document.getElementById('deckContainer');
    var decks = JSON.parse(localStorage.getItem('decks') || '[]');
    decks.forEach(deck => {
        localStorage.setItem(deck.id, JSON.stringify(deck));
        var block = document.createElement("button");
        block.setAttribute('id', deck.id);
        block.setAttribute('onclick', "localStorage.setItem('activeDeck', " + deck.id +"), location.href=\'/deck\'");
        block.innerHTML = deck.deckName;
        container.appendChild(block);
    });
}

async function  loadDeck() {
    var deck = JSON.parse(localStorage.getItem(localStorage.getItem('activeDeck')));
    var title = document.getElementById('title');
    title.innerHTML = deck.deckName;

    const container = document.getElementById('deckContainer');
    var header = document.createElement('h1');
    header.innerHTML = localStorage.getItem('username') + '\'s ' + deck.deckName;
    container.appendChild(header);

    var cards = deck.cardsInDeck || '[]';
    cards.forEach(card => {
        var a = document.createElement('a');
        a.setAttribute('href',"/login");
        var cardElement = document.createElement('img');
        cardElement.src = card.pngUri;
        a.appendChild(cardElement);
        container.appendChild(a);
    });


}


