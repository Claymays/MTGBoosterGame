import * as constants from './shared.js';
import {$, setUser} from './shared.js';

window.onload = () => {
    let container = $('#content');

    let username = document.createElement('input');
    username.id = 'username';
    username.autocomplete = 'username';
    container.append(username);

    let password = document.createElement('input');
    password.id = 'password';
    password.type = 'password';
    password.minLength = 3;
    password.maxLength = 15;
    container.append(password)

    let authButton = document.createElement('button');
    authButton.textContent = 'Login';
    authButton.addEventListener('click', userAuth);
    container.append(authButton);

    let newUserButton = document.createElement('button');
    newUserButton.textContent = 'Create Account';
    newUserButton.addEventListener('click', createUser);
    container.append(newUserButton);
}

function createUser() {
    let params = {
        username: $('#username').value,
        password: $('#password').value
    };

    fetch(constants.paths.users, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    })
        .then(response => {return response.json()})
        .then(user => {
            setUser(user);
            location.href = 'userPage';
        })
    .catch(error => {
        alert('Error creating account. Please try again');
        console.log(error);
    });
}

async function userAuth() {
    let params = {
        username: $('#username').value,
        password: $('#password').value
    };

    fetch(constants.paths.users_auth, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    })
        .then(data => {
            return data.json()
        })
        .then(user => {
            setUser(user);
            location.href = '/userPage';
        })
        .catch(error => {
            alert('Error logging in. Please try again');
    })
}









