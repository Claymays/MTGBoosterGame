export const api_base_path = '/api';
export const paths = {
    login_page: '/login',
    card_page: '/card',
    user_page: '/userPage',
    deck_page: '/deck',
    cards: `${api_base_path}/card`,
    decks: `${api_base_path}/deck`,
    users: `${api_base_path}/user`,
    users_auth: `${api_base_path}/user/login`,
    users_create: `${api_base_path}/user/create`
}

export const set = (key, value) => localStorage.setItem(key, value);
export const get = (key) => localStorage.getItem(key);

export const $ = (selector) => document.querySelector(selector);

export function setUser(token) {
    set('token', token);
}
export function logout() {
    localStorage.clear();
    location.href = '/login';
}

