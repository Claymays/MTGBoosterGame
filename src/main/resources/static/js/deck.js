import * as constants from './shared.js';
import { $, get, set} from "./shared.js"

window.onload = () => { loadDeck(); }

async function loadDeck() {
    let title = $('#title');
    let header = $('#header');

    await loadSearchBar();

    cardTypeDiv('enchantments');
    cardTypeDiv('sorceries');
    cardTypeDiv('creatures');
    cardTypeDiv("planeswalkers");
    cardTypeDiv('instants');
    cardTypeDiv('lands');
    cardTypeDiv('artifacts');

    let deckId = get('activeDeck');
    let deck;
    for (let i = 0; i < constants.user.decks.length; i++) {
        if (constants.user.decks[i].id === deckId) {
            deck = constants.user.decks[i];
        }
    }

    title.innerText = deck.deckName;
    let deckTitle = document.createElement('span');
    deckTitle.innerText = deck.deckName;
    header.appendChild(deckTitle);

    let cards = deck.cardsInDeck || [];

    cards.forEach(card => {
        loadCard(card);
    });

}

async function cardSearch() {
    if (get('card') != null) {
        localStorage.removeItem('card')
        let oldCard = $('card')
        let btn = $('addButton');
        if (btn != null) {btn.remove();}
        if (oldCard != null) {
            oldCard.remove();
        }
    }

    let cardSearchParams = '?name=' + $('searchBar').value;
    let searchUrl = constants.paths.cards + cardSearchParams;

    const card = await fetch(searchUrl)
        .then(response => {
            return response.json();
        });

    set('card', JSON.stringify(card));

    let header = $('deckHeader');

    let img = document.createElement('img');
    img.id = 'card';
    img.src = card.pngUri;

    let addButton = document.createElement('button');
    addButton.id = 'addButton';
    addButton.textContent = '+';
    addButton.addEventListener('click', function() {
        addCardToDeck(card, JSON.parse(get('activeDeck')))
    });

    header.appendChild(img);
    header.appendChild(addButton);

}

function loadCard(card) {
    let a = document.createElement('a');
    a.setAttribute('href',"/card");
    let cardElement = document.createElement('img');
    cardElement.src = card.pngUri;
    let container = document.getElementById('deckContainer');

    a.appendChild(cardElement);

    if (card.typeLine.includes('Creature')) {
        let creatures = $('#creatures')
        creatures.style.display = 'inline';
        creatures.appendChild(a);
        container.appendChild(creatures);
    }
    else if (card.typeLine.includes('Planeswalker')) {
        let planeswalkers = $('#planeswalkers');
        planeswalkers.style.display = 'inline';
        planeswalkers.appendChild(a);
        container.appendChild(planeswalkers);
    }
    else if (card.typeLine.includes('Enchantment')) {
        let enchantments = $('#enchantments');
        enchantments.style.display = 'inline';
        enchantments.appendChild(a);
        container.appendChild(enchantments)
    }
    else if (card.typeLine.includes('Sorcery')) {
        let sorceries = $('#sorceries');
        sorceries.style.display = 'inline';
        sorceries.appendChild(a);
        container.appendChild(sorceries);
    }
    else if (card.typeLine.includes('Instant')) {
        let instants = $('#instants');
        instants.style.display = 'inline';
        instants.appendChild(a);
        container.appendChild(instants);
    }
    else if (card.typeLine.includes('Land')) {
        let lands = $('#lands');
        lands.style.display = 'inline';
        lands.appendChild(a);
        container.appendChild(lands);
    }
    else if (card.typeLine.includes('Artifact')) {
        let artifacts = $('#artifacts');
        artifacts.style.display = 'inline';
        artifacts.appendChild(a);
        container.appendChild(artifacts);
    }
}

async function addCardToDeck(card, deckID) {
    let params = {
        cardName: card.name,
        deckId: deckID
    };
    let user = JSON.parse(get('user'));
    for (let i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id === deckID) {
            user.decks[i].cardsInDeck.push(card);
            localStorage.setItem('user', JSON.stringify(user));
        }
    }
    let test = await fetch('http://localhost:8080/api/card/?cardName=' + card.name + '&deckId=' + deckID,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(params),
        }).then(response => {return response.json();});

    loadCard(test);

}

function cardTypeDiv(name) {
    let container = $('#cardContainer');

    let div = document.createElement('div');
    div.id = name;
    div.textContent = name;
    div.style.display = 'none';
    container.append(div);
}

async function deleteDeck() {
    let deckId = localStorage.getItem('activeDeck');
    let url = baseUrl + '/api/deck/' + deckId;
    await fetch(url, {
        headers: {
            'Authorization': 'bearer' + localStorage.getItem('token'),
        },
        method: 'DELETE',
    });
    let user = JSON.parse(localStorage.getItem('user'));
    for (let i = 0; i < user.decks.length; i = i + 1) {
        if (user.decks[i].id == deckId) {
            user.decks.splice(i, 1);
        }
    }
    localStorage.setItem('user', JSON.stringify(user));
    location.href = '/userPage';
}

async function loadSearchBar() {
    let header = $('#header');

    let cardSearchBar = document.createElement('input');
    cardSearchBar.id = 'searchBar';
    cardSearchBar.autocomplete = 'Card Search';
    header.append(cardSearchBar);

    let submitButton = document.createElement('button');
    submitButton.textContent = 'submit';
    submitButton.addEventListener('click',  async function() {
        let card = await fetch(constants.paths.cards).then(response => {return response.json();});
        await cardSearch(card, get('activeDeck'));
    });
    header.append(submitButton);
}