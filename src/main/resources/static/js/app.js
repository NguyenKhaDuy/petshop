import { qs, qsa, cloneView, renderIcons, toast, closeModal, initials } from './utils.js';
import { state, loadSession, loadCurrentUser, loadCustomerCollections, clearSession, isAdmin } from './state.js';
import { renderHome, renderProducts, renderProductDetail } from './modules/store.js';
import { renderLogin, renderRegister } from './modules/auth.js';
import { renderCart, renderWishlist, renderCustomerOrders, renderProfile } from './modules/customer.js';
import {
    renderAdminDashboard, renderAdminProducts, renderAdminCategories, renderAdminSizes,
    renderAdminVouchers, renderAdminImports, renderAdminOrders, renderAdminUsers
} from './modules/admin.js';

const routes = {
    home: { view: 'home', render: renderHome, public: true },
    products: { view: 'products', render: renderProducts, public: true },
    product: { view: 'product-detail', render: renderProductDetail, public: true },
    login: { view: 'login', render: renderLogin, guest: true },
    register: { view: 'register', render: renderRegister, guest: true },
    cart: { view: 'cart', render: renderCart, auth: true },
    wishlist: { view: 'wishlist', render: renderWishlist, auth: true },
    orders: { view: 'customer-orders', render: renderCustomerOrders, auth: true },
    profile: { view: 'profile', render: renderProfile, auth: true },
    admin: { view: 'admin-dashboard', render: renderAdminDashboard, admin: true },
    'admin/products': { view: 'admin-products', render: renderAdminProducts, admin: true },
    'admin/categories': { view: 'admin-categories', render: renderAdminCategories, admin: true },
    'admin/sizes': { view: 'admin-sizes', render: renderAdminSizes, admin: true },
    'admin/vouchers': { view: 'admin-vouchers', render: renderAdminVouchers, admin: true },
    'admin/imports': { view: 'admin-imports', render: renderAdminImports, admin: true },
    'admin/orders': { view: 'admin-orders', render: renderAdminOrders, admin: true },
    'admin/users': { view: 'admin-users', render: renderAdminUsers, admin: true }
};

const publicNav = [
    ['Khám phá', null, null],
    ['Trang chủ', '#/home', 'home'],
    ['Sản phẩm', '#/products', 'store'],
    ['Cá nhân', null, null],
    ['Yêu thích', '#/wishlist', 'heart'],
    ['Giỏ hàng', '#/cart', 'shopping-bag'],
    ['Đơn hàng', '#/orders', 'clipboard-list'],
    ['Tài khoản', '#/profile', 'user']
];

const adminNav = [
    ['Vận hành', null, null],
    ['Tổng quan', '#/admin', 'grid'],
    ['Sản phẩm', '#/admin/products', 'package'],
    ['Đơn hàng', '#/admin/orders', 'clipboard-list'],
    ['Kho hàng', null, null],
    ['Nhập kho', '#/admin/imports', 'warehouse'],
    ['Danh mục', '#/admin/categories', 'tags'],
    ['Kích cỡ', '#/admin/sizes', 'ruler'],
    ['Khuyến mãi', '#/admin/vouchers', 'ticket'],
    ['Khách hàng', '#/admin/users', 'users'],
    ['Cửa hàng', null, null],
    ['Xem trang bán hàng', '#/home', 'store']
];

function parseRoute() {
    const raw = location.hash.replace(/^#\/?/, '') || 'home';
    const [path] = raw.split('?');
    const parts = path.split('/').filter(Boolean);
    if (parts[0] === 'product' && parts[1]) return { key: 'product', params: { id: parts[1] } };
    const key = parts.slice(0, 2).join('/');
    return { key: routes[key] ? key : parts[0], params: {} };
}

function renderNav(activeKey) {
    const nav = qs('#sidebar-nav');
    const items = isAdmin() ? adminNav : publicNav;
    nav.innerHTML = items.map(([label, href, icon]) => {
        if (!href) return `<span class="nav-section-label">${label}</span>`;
        const route = href.replace('#/', '');
        const active = activeKey === route || (route === 'products' && activeKey === 'product');
        return `<a class="nav-item${active ? ' is-active' : ''}" href="${href}">
            <span data-icon="${icon}"></span><span>${label}</span></a>`;
    }).join('');
    renderIcons(nav);
}

function renderAccount() {
    const header = qs('#header-account');
    const sidebar = qs('#sidebar-user');
    if (!state.user) {
        header.innerHTML = `<a class="account-login" href="#/login"><span data-icon="user"></span><span>Đăng nhập</span></a>`;
        sidebar.innerHTML = `<a class="nav-item" href="#/login"><span data-icon="log-out"></span><span>Đăng nhập</span></a>`;
    } else {
        const adminClass = isAdmin() ? ' admin' : '';
        const accountTarget = isAdmin() ? '#/admin' : '#/profile';
        header.innerHTML = `<a class="account-trigger" href="${accountTarget}">
            <span class="avatar${adminClass}">${initials(state.user.name)}</span>
            <span class="account-copy"><strong>${state.user.name}</strong><span>${isAdmin() ? 'Quản trị viên' : 'Người dùng'}</span></span></a>`;
        sidebar.innerHTML = `<div class="account-trigger"><span class="avatar${adminClass}">${initials(state.user.name)}</span>
            <span class="account-copy"><strong>${state.user.name}</strong><span>${state.user.email}</span></span>
            <button class="icon-button" data-action="logout" type="button" aria-label="Đăng xuất" data-icon="log-out"></button></div>`;
        qs('[data-action="logout"]', sidebar)?.addEventListener('click', logout);
    }
    renderIcons(header);
    renderIcons(sidebar);
}

function updateCounters() {
    const count = state.cart?.cartItems?.reduce((sum, item) => sum + Number(item.quantity || 0), 0) || 0;
    const element = qs('#cart-count');
    element.textContent = count > 99 ? '99+' : count;
    element.classList.toggle('is-hidden', count === 0);
}

export async function refreshShell({ collections = false } = {}) {
    try {
        await loadCurrentUser();
        if (collections && state.user && !isAdmin()) await loadCustomerCollections(true);
    } catch {
        clearSession();
    }
    renderAccount();
    updateCounters();
}

function logout() {
    clearSession();
    renderAccount();
    updateCounters();
    toast('Bạn đã đăng xuất an toàn.');
    location.hash = '#/home';
}

function forbiddenView() {
    return `<div class="empty-state"><span class="empty-icon" data-icon="shield-check"></span>
        <h3>Bạn không có quyền truy cập</h3><p>Khu vực này chỉ dành cho tài khoản quản trị viên.</p>
        <a class="button button-primary" href="#/home">Về cửa hàng</a></div>`;
}

async function navigate() {
    closeMobileSidebar();
    closeModal();
    const { key, params } = parseRoute();
    const route = routes[key] || routes.home;
    if (route.auth && !state.session) {
        sessionStorage.setItem('pawcare.redirect', location.hash);
        location.hash = '#/login';
        return;
    }
    if (route.guest && state.session) {
        location.hash = isAdmin() ? '#/admin' : '#/home';
        return;
    }
    const root = qs('#app-view');
    const loader = qs('#route-loading');
    root.innerHTML = '';
    loader.classList.remove('is-hidden');
    renderNav(key);
    try {
        if (route.admin && !isAdmin()) {
            root.innerHTML = forbiddenView();
        } else {
            root.appendChild(cloneView(route.view));
            renderIcons(root);
            await route.render({ root, params, refreshShell });
        }
    } catch (error) {
        console.error(error);
        root.innerHTML = `<div class="empty-state"><span class="empty-icon" data-icon="triangle-alert"></span>
            <h3>Chưa thể tải màn hình</h3><p>${error.message || 'Vui lòng thử lại.'}</p>
            <button class="button button-primary" data-action="retry"><span data-icon="refresh"></span> Thử lại</button></div>`;
        qs('[data-action="retry"]', root)?.addEventListener('click', navigate);
        renderIcons(root);
    } finally {
        loader.classList.add('is-hidden');
        renderIcons(root);
        root.querySelector('h1, h2')?.setAttribute('tabindex', '-1');
        window.scrollTo({ top: 0, behavior: 'instant' });
    }
}

function openMobileSidebar() {
    qs('#sidebar').classList.add('is-open');
    qs('#sidebar-backdrop').classList.add('is-open');
}
function closeMobileSidebar() {
    qs('#sidebar').classList.remove('is-open');
    qs('#sidebar-backdrop').classList.remove('is-open');
}

function setupGlobalEvents() {
    qs('#mobile-menu-button').addEventListener('click', openMobileSidebar);
    qs('#sidebar-backdrop').addEventListener('click', closeMobileSidebar);
    qs('#modal-close').addEventListener('click', closeModal);
    qs('#app-modal').addEventListener('click', (event) => { if (event.target.id === 'app-modal') closeModal(); });
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') { closeModal(); closeMobileSidebar(); }
    });
    window.addEventListener('hashchange', navigate);
}

async function init() {
    renderIcons();
    qs('#footer-year').textContent = new Date().getFullYear();
    loadSession();
    await refreshShell({ collections: true });
    setupGlobalEvents();
    await navigate();
}

init();
