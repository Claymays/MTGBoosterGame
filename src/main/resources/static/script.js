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
        setUser(user);
    }


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
    const decks = user.decks || [];

    title.innerText = user.username;

    decks.forEach(deck => {
        var block = document.createElement("button");
        block.setAttribute('id', deck.id);
        block.innerText = deck.deckName;
        container.appendChild(block);
        block.addEventListener('click', function(event) {
            localStorage.setItem('activeDeck', deck.id);
            location.href='/deck';
        });
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
    setUser(user);
}

function loadDeck() {
    const user = JSON.parse(localStorage.getItem('token'));
    var activeDeck = localStorage.getItem('activeDeck');
    var deck;
    for (let i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id == activeDeck) {
            deck = user.decks[i];
        }
    };
    var title = document.getElementById('title');
    var header = document.getElementById('deckHeader');
    const container = document.getElementById('deckContainer');

    title.innerText = deck.deckName;
    var deckTitle = document.createElement('span');
    deckTitle.innerText = deck.deckName;
    header.appendChild(deckTitle);

    var cards = deck.cardsInDeck || [];

    cards.forEach(card => {
        var a = document.createElement('a');
        a.setAttribute('href',"/card");
        var cardElement = document.createElement('img');
        cardElement.src = card.pngUri;
        a.appendChild(cardElement);
        container.appendChild(a);
    });

}

async function deleteDeck() {
    var deckId = localStorage.getItem('activeDeck');
    var url = baseUrl + '/api/deck/' + deckId;
    await fetch(url, {
        method: 'DELETE',
        });
    var user = JSON.parse(localStorage.getItem('token'));
    for (let i = 0; i < user.decks.length; i = i + 1) {
        if (user.decks[i].id == deckId) {
            user.decks.splice(i, 1);
        }
    }
    localStorage.setItem(user.token, JSON.stringify(user));
    location.href = '/userPage';
}

async function cardSearch() {
    if (localStorage.getItem('card') != null) {
        localStorage.removeItem('card')
        var oldCard = document.getElementById('card')
        if (oldCard != null) {
            oldCard.remove();
        }
    }
    var cardSearchParams = '?name=' + document.getElementById('searchBar').value;
    var searchUrl = baseUrl + '/api/card/' + cardSearchParams;
    const card = await fetch(searchUrl)
    .then(response => {
        return response.json();
    });

    localStorage.setItem('card', JSON.stringify(card));
    var header = document.getElementById('deckHeader');
    var img = document.createElement('img');
    img.setAttribute('id', 'card');
    img.src = card.pngUri;
    var add = document.createElement('button');
    add.innerText = '+';
    add.addEventListener('click', function() {addCardToDeck(card, JSON.parse(localStorage.getItem('activeDeck')))});
    header.appendChild(img);
    header.appendChild(add);

}
async function addCardToDeck(card, deckID) {
    var params = {
        cardName: card.name,
        deckId: deckID
    };
    var user = JSON.parse(localStorage.getItem('token'));
    for (var i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id == deckID) {
            user.decks[i].cardsInDeck.push(card);
            localStorage.setItem('token', JSON.stringify(user));
        }
    }
    var test = await fetch('http://localhost:8080/api/card/?cardName=' + card.name + '&deckId=' + deckID,
    {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {return response.json();}).then(loadDeck());

}
