export const api_base_path = '/api';
export const paths = {
    login_page: '/login',
    card_page: '/card',
    user_page: '/userPage',
    deck_page: '/deck',
    cards: `${api_base_path}/card`,
    decks: `${api_base_path}/deck`,
    users: `${api_base_path}/user`,
    users_auth: `${api_base_path}/user/login`
}

export const set = (key, value) => localStorage.setItem(key, value);
export const get = (key) => localStorage.getItem(key);

export const $ = (selector) => document.querySelector(selector);

export function setUser(user) {
    set('token', user.token);
    set('user', JSON.stringify(user));
}
export function logout() {
    localStorage.clear();
    location.href = '/login';
}

// TODO: figure out why we are being denied access, even with a new token.
export async function checkAuth() {
    let searchUrl = 'http://localhost:8080/api/user/test';
    await fetch(searchUrl, {
        method: 'POST',
        headers: {
            'Authorization': 'bearer' + get('token'),
            'Content-Type': 'application/json',
        }
    })
        .then(response => {return response.json()})
        .then(json => {
            console.debug('Authorization response:', json);
            if (json.error) {
                logout();
            }
        });
}

