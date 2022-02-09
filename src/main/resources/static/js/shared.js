export const origin = 'http://localhost:8080';
export const api_base_path = '/api';
export const paths = {
    login: '/login',
    card_page: '/card',
    user_page: '/userPage',
    deck_page: '/deck',
    cards: '${api_base_path}/card',
    decks: '${api_base_path}/deck',
    users: '${api_base_path}/user'
}
export const set = (key, value) => localStorage.setItem(key, value);
export const get = (key) => localStorage.getItem(key);

export const $ = (selector) => document.querySelector(selector);
export const $all = (selector) => document.querySelectorAll(selector);

