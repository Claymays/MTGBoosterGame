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

async function createUser() {
    let params = {
        username: $('#username').value,
        password: $('#password').value
    };

    const user = await fetch(constants.paths.users, {
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
        await setUser(user);
    } else {
        prompt('Error creating account. Please try again');
    }

    location.href = 'userPage';
}

async function userAuth() {
    let params = {
        username: $('#username').value,
        password: $('#password').value
    };

    let response = await fetch(constants.paths.users_auth, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    })
    .then(data => {return data.json()});
    if (response != null) {
        setUser(response);
        location.href = '/userPage';
    } else {
        prompt('Error logging in. Please try again');
    }

}









