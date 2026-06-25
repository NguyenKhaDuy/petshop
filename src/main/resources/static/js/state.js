import { api } from './api.js';

const SESSION_KEY = 'pawcare.session';

export const state = {
    session: null,
    user: null,
    products: null,
    categories: null,
    wishlist: [],
    cart: null
};

export function loadSession() {
    try {
        state.session = JSON.parse(localStorage.getItem(SESSION_KEY) || 'null');
    } catch {
        state.session = null;
    }
    return state.session;
}

export function saveSession(session) {
    state.session = session;
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
    state.session = null;
    state.user = null;
    state.wishlist = [];
    state.cart = null;
    localStorage.removeItem(SESSION_KEY);
}

export async function loadCurrentUser(force = false) {
    if (!state.session?.idUser) return null;
    if (state.user && !force) return state.user;
    const response = await api.user(state.session.idUser);
    state.user = response.data;
    return state.user;
}

export async function loadProducts(force = false) {
    if (state.products && !force) return state.products;
    state.products = (await api.products()).data || [];
    return state.products;
}

export async function loadCategories(force = false) {
    if (state.categories && !force) return state.categories;
    state.categories = (await api.categories()).data || [];
    return state.categories;
}

export async function loadCustomerCollections(force = false) {
    if (!state.session?.idUser) return;
    const tasks = [];
    if (force || !state.wishlist.length) {
        tasks.push(api.wishlist(state.session.idUser).then((response) => { state.wishlist = response.data || []; }).catch(() => { state.wishlist = []; }));
    }
    if (force || !state.cart) {
        tasks.push(api.cart(state.session.idUser).then((response) => { state.cart = response.data; }).catch(() => { state.cart = null; }));
    }
    await Promise.all(tasks);
}

export function isAdmin() {
    return String(state.user?.role || '').toUpperCase() === 'ADMIN';
}
