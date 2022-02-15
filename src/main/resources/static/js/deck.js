import * as constants from './shared.js';
import {$, get, set, setUser} from "./shared.js"

window.onload = () => { loadDeck(); }

let user;

async function loadDeck() {
    let title = $('#title');
    let header = $('#header');

//  Attach a search bar to the header.
    await loadSearchBar();

//  Attach a delete button to the header.
    let deleteButton = document.createElement('button');
    deleteButton.textContent = 'Delete deck';
    deleteButton.addEventListener('click', deleteDeck);
    header.append(deleteButton);

//  Attach hidden card types containers to the body.
    cardTypeDiv('enchantments');
    cardTypeDiv('sorceries');
    cardTypeDiv('creatures');
    cardTypeDiv("planeswalkers");
    cardTypeDiv('instants');
    cardTypeDiv('lands');
    cardTypeDiv('artifacts');

//  Pull the chosen deck from storage
    user = JSON.parse(get('user'));
    let deckId = JSON.parse(get('activeDeck'));
    let activeDeck;
    for (let i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id === deckId) {
            activeDeck = user.decks[i];
        }
    }

    title.innerText = activeDeck.deckName;

    let deckTitle = document.createElement('span');
    deckTitle.innerText = activeDeck.deckName;
    header.appendChild(deckTitle);

//  Parse each card and assign it to a cardTypeDiv.
    let cards = activeDeck.cardsInDeck || [];
    cards.forEach(card => {
        loadCard(card);
    });

}

async function cardSearch() {
    if (get('card') != null) {
        localStorage.removeItem('card')
        let oldCard = $('#card')
        let btn = $('#addButton');
        if (btn != null) {btn.remove();}
        if (oldCard != null) {
            oldCard.remove();
        }
    }

    let cardSearchParams = '?name=' + $('#searchBar').value;
    let searchUrl = constants.paths.cards + cardSearchParams;

    const card = await fetch(searchUrl)
        .then(response => {
            return response.json();
        });

    set('card', JSON.stringify(card));

    let header = $('#header');

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

    a.appendChild(cardElement);

    if (card.typeLine.includes('Creature')) {
        let creatures = $('#creatures')
        creatures.style.display = 'inline';
        creatures.appendChild(a);
    }
    else if (card.typeLine.includes('Planeswalker')) {
        let planeswalkers = $('#planeswalkers');
        planeswalkers.style.display = 'inline';
        planeswalkers.appendChild(a);
    }
    else if (card.typeLine.includes('Enchantment')) {
        let enchantments = $('#enchantments');
        enchantments.style.display = 'inline';
        enchantments.appendChild(a);
    }
    else if (card.typeLine.includes('Sorcery')) {
        let sorceries = $('#sorceries');
        sorceries.style.display = 'inline';
        sorceries.appendChild(a);
    }
    else if (card.typeLine.includes('Instant')) {
        let instants = $('#instants');
        instants.style.display = 'inline';
        instants.appendChild(a);
    }
    else if (card.typeLine.includes('Land')) {
        let lands = $('#lands');
        lands.style.display = 'inline';
        lands.appendChild(a);
    }
    else if (card.typeLine.includes('Artifact')) {
        let artifacts = $('#artifacts');
        artifacts.style.display = 'inline';
        artifacts.appendChild(a);
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
    let container = $('#content');

    let div = document.createElement('div');
    div.id = name;
    div.textContent = name;
    div.style.display = 'none';
    container.append(div);
}

async function deleteDeck() {
    let deckId = get('activeDeck');

    let url = constants.paths.decks + '/' + deckId;
    await fetch(url, {
        headers: {
            'Authorization': 'bearer' + get('token'),
        },
        method: 'DELETE',
    });

    let user = JSON.parse(get('user'));
    for (let i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id == deckId) {
            user.decks.splice(i, 1);
        }
    }
    setUser(user);

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
    submitButton.addEventListener('click',  function() {
        fetch(constants.paths.cards)
            .then(response => {
                return response.json();
            })
        .then(card => {
            cardSearch(card, get('activeDeck'));
        })
    })
    header.append(submitButton);
}