import * as constants from './shared.js';
import {$, checkAuth, get, set, setUser} from "./shared.js"

window.onload = () => {
    checkAuth().then(() => loadDeck());
}

let user;
let container = $('#content');

async function loadDeck() {
    let title = $('#title');
    let header = $('#header');

//  Attach a search bar to the header.
    loadSearchBar();

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

async function loadSearchBar() {
    let header = $('#header');

    let cardSearchBar = document.createElement('input');
    cardSearchBar.id = 'searchBar';
    cardSearchBar.autocomplete = 'Card Search';
    header.append(cardSearchBar);

    let submitButton = document.createElement('button');
    submitButton.textContent = 'submit';
    submitButton.addEventListener('click',  function() {
        let searchParam = '?name=' + $('#searchBar').value;
        fetch(constants.paths.cards + searchParam, {
            headers: {
                'Authorization': 'bearer' + get('token')
            }
        })
            .then(response => {
                return response.json();
            })
            .then(card => {
                cardSearch(card, get('activeDeck'));
            })
    })
    header.append(submitButton);
}

function loadCard(card) {
    /*
     Create a card image with a link to its own page.
     Attach the image to the appropriate cardTypeDiv.
    */
    // TODO: figure out how to make a page for the cards
    // TODO: Make custom groupings for cards.

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

async function addCardToDeck(card, deckID)  {
    let params = {
        cardName: card.name,
        deckId: deckID
    };

    let user = JSON.parse(get('user'));
    for (let i = 0; i < user.decks.length; i++) {
        if (user.decks[i].id === deckID) {
            user.decks[i].cardsInDeck.push(card);
            setUser(user);
        }
    }

    fetch(constants.paths.cards, {
            method: 'POST',
            headers: {
                'Authorization': 'bearer' + get('token'),
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(params),
        })
        .then(response => {return response.json();})
        .then(card => {loadCard(card);});
}

function cardSearch() {
    // Check to see if there is a previous search, and remove it.
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

    fetch(searchUrl, {
        headers: {
            'Authorization': 'bearer' + get('token'),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            return response.json();
        })
        .then(card => {

            set('card', JSON.stringify(card));

            let header = $('#header');

            let img = document.createElement('img');
            img.id = 'card';
            img.src = card.pngUri;

            let addCardButton = document.createElement('button');
            addCardButton.id = 'addButton';
            addCardButton.textContent = '+';
            addCardButton.addEventListener('click', function() {
                addCardToDeck(JSON.parse(get('card')), JSON.parse(get('activeDeck')))
            });

            header.appendChild(img);
            header.appendChild(addCardButton);
        });
}

function cardTypeDiv(name) {
    let cardTypeDiv = document.createElement('div');
    cardTypeDiv.id = name;
    cardTypeDiv.textContent = name;
    cardTypeDiv.style.display = 'none';
    container.append(cardTypeDiv);
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
