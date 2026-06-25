const ICONS = {
    menu: '<path d="M4 6h16M4 12h16M4 18h16"/>',
    paw: '<circle cx="8" cy="7" r="2.2"/><circle cx="16" cy="7" r="2.2"/><circle cx="5.5" cy="12" r="2"/><circle cx="18.5" cy="12" r="2"/><path d="M8 18c0-3 1.8-5 4-5s4 2 4 5c0 2-1.4 3-4 3s-4-1-4-3Z"/>',
    search: '<circle cx="11" cy="11" r="7"/><path d="m20 20-4-4"/>',
    'shopping-bag': '<path d="M6 8h12l1 12H5L6 8Z"/><path d="M9 9V6a3 3 0 0 1 6 0v3"/>',
    heart: '<path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/>',
    headphones: '<path d="M4 14v-2a8 8 0 0 1 16 0v2"/><path d="M18 19c0 1.1-.9 2-2 2h-1v-7h3a2 2 0 0 1 2 2v1a2 2 0 0 1-2 2ZM6 19a2 2 0 0 1-2-2v-1a2 2 0 0 1 2-2h3v7H8a2 2 0 0 1-2-2Z"/>',
    x: '<path d="M18 6 6 18M6 6l12 12"/>',
    sparkles: '<path d="m12 3 1.3 3.7L17 8l-3.7 1.3L12 13l-1.3-3.7L7 8l3.7-1.3L12 3ZM5 14l.8 2.2L8 17l-2.2.8L5 20l-.8-2.2L2 17l2.2-.8L5 14Zm14-1 .8 2.2L22 16l-2.2.8L19 19l-.8-2.2L16 16l2.2-.8L19 13Z"/>',
    'arrow-right': '<path d="M5 12h14M13 6l6 6-6 6"/>',
    'arrow-left': '<path d="M19 12H5M11 18l-6-6 6-6"/>',
    'shield-check': '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z"/><path d="m9 12 2 2 4-4"/>',
    truck: '<path d="M3 6h11v10H3zM14 10h4l3 3v3h-7z"/><circle cx="7" cy="18" r="2"/><circle cx="18" cy="18" r="2"/>',
    stethoscope: '<path d="M6 3v5a4 4 0 0 0 8 0V3M4 3h4M12 3h4M10 17a4 4 0 0 0 8 0v-1"/><circle cx="19" cy="13" r="2"/>',
    'chevron-right': '<path d="m9 18 6-6-6-6"/>',
    'chevron-left': '<path d="m15 18-6-6 6-6"/>',
    layers: '<path d="m12 2 9 5-9 5-9-5 9-5Z"/><path d="m3 12 9 5 9-5M3 17l9 5 9-5"/>',
    'arrow-up-down': '<path d="m7 15-3 3 3 3M4 18V3M17 9l3-3-3-3M20 6v15"/>',
    mail: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="m3 7 9 6 9-6"/>',
    lock: '<rect x="4" y="10" width="16" height="11" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/>',
    eye: '<path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z"/><circle cx="12" cy="12" r="2.5"/>',
    quote: '<path d="M3 21c3 0 7-1 7-8V5H3v8h4c0 3-1 4-4 4v4Zm11 0c3 0 7-1 7-8V5h-7v8h4c0 3-1 4-4 4v4Z"/>',
    'check-circle': '<circle cx="12" cy="12" r="9"/><path d="m8 12 3 3 5-6"/>',
    plus: '<path d="M12 5v14M5 12h14"/>',
    minus: '<path d="M5 12h14"/>',
    calendar: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18"/>',
    'package-plus': '<path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z"/><path d="m4.5 7.8 7.5 4.3 7.5-4.3M12 12v9M16 3.9 8 8.5"/><path d="M18 14v5M15.5 16.5h5"/>',
    warehouse: '<path d="M3 21V8l9-5 9 5v13M7 21v-8h10v8M7 17h10"/>',
    ticket: '<path d="M3 9a3 3 0 0 0 0 6v4h18v-4a3 3 0 0 0 0-6V5H3v4Z"/><path d="M13 5v2M13 10v2M13 15v2M13 20v1"/>',
    filter: '<path d="M4 5h16M7 12h10M10 19h4"/>',
    map: '<path d="m3 6 6-3 6 3 6-3v15l-6 3-6-3-6 3V6Z"/><path d="M9 3v15M15 6v15"/>',
    'triangle-alert': '<path d="m12 3 10 18H2L12 3Z"/><path d="M12 9v5M12 18h.01"/>',
    home: '<path d="m3 11 9-8 9 8v10h-6v-6H9v6H3V11Z"/>',
    grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
    user: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
    users: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.9M16 3.1a4 4 0 0 1 0 7.8"/>',
    'clipboard-list': '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 4V2h6v2M9 10h6M9 14h6M9 18h4"/>',
    tags: '<path d="M20 13 11 4H4v7l9 9 7-7Z"/><circle cx="7.5" cy="7.5" r="1"/>',
    ruler: '<path d="m3 17 14-14 4 4L7 21l-4-4Z"/><path d="m14 6 4 4M11 9l2 2M8 12l4 4M5 15l2 2"/>',
    box: '<path d="m12 2 9 5-9 5-9-5 9-5Z"/><path d="M3 7v10l9 5 9-5V7M12 12v10"/>',
    receipt: '<path d="M5 3v18l3-2 3 2 3-2 3 2 2-1V3l-2 1-3-1-3 1-3-1-3 1Z"/><path d="M8 8h8M8 12h8M8 16h5"/>',
    edit: '<path d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4L16.5 3.5Z"/>',
    trash: '<path d="M3 6h18M8 6V4h8v2M19 6l-1 15H6L5 6M10 11v5M14 11v5"/>',
    clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    package: '<path d="m12 2 9 5-9 5-9-5 9-5Z"/><path d="M3 7v10l9 5 9-5V7M12 12v10"/>',
    'credit-card': '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 10h18M7 15h2"/>',
    'map-pin': '<path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/>',
    phone: '<path d="M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 4.2 2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c.1 1 .4 2 .7 2.9a2 2 0 0 1-.5 2.1L8 10a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.5c.9.3 1.9.6 2.9.7a2 2 0 0 1 1.7 2Z"/>',
    star: '<path d="m12 2 3.1 6.3 6.9 1-5 4.9 1.2 6.8-6.2-3.2L5.8 21 7 14.2l-5-4.9 6.9-1L12 2Z"/>',
    check: '<path d="m5 12 4 4L19 6"/>',
    'alert-circle': '<circle cx="12" cy="12" r="9"/><path d="M12 8v5M12 17h.01"/>',
    info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v5M12 8h.01"/>',
    image: '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="8.5" cy="9" r="1.5"/><path d="m21 15-5-5L5 20"/>',
    refresh: '<path d="M20 7h-5V2M4 17h5v5"/><path d="M5.5 9a7 7 0 0 1 11.8-3L20 7M4 17l2.7 1a7 7 0 0 0 11.8-3"/>',
    store: '<path d="M3 9h18l-2-5H5L3 9Z"/><path d="M5 9v11h14V9M9 20v-6h6v6"/><path d="M3 9a3 3 0 0 0 6 0 3 3 0 0 0 6 0 3 3 0 0 0 6 0"/>',
    'log-out': '<path d="M10 17l5-5-5-5M15 12H3M15 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4"/>',
    'more-vertical': '<circle cx="12" cy="5" r="1"/><circle cx="12" cy="12" r="1"/><circle cx="12" cy="19" r="1"/>'
};

export const qs = (selector, root = document) => root.querySelector(selector);
export const qsa = (selector, root = document) => [...root.querySelectorAll(selector)];

export function renderIcons(root = document) {
    qsa('[data-icon]', root).forEach((element) => {
        if (element.dataset.iconReady === 'true') return;
        const body = ICONS[element.dataset.icon] || ICONS.info;
        element.innerHTML = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${body}</svg>`;
        element.dataset.iconReady = 'true';
    });
}

export function cloneView(name) {
    const template = document.getElementById(`view-${name}`);
    if (!template) throw new Error(`Không tìm thấy giao diện: ${name}`);
    return template.content.cloneNode(true);
}

export function escapeHtml(value = '') {
    return String(value).replace(/[&<>"']/g, (char) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
    }[char]));
}

export function formatCurrency(value) {
    const number = Number(value || 0);
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(number);
}

export function formatDate(value, includeTime = false) {
    if (!value) return '—';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value).replace('T', ' ');
    return new Intl.DateTimeFormat('vi-VN', includeTime
        ? { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }
        : { day: '2-digit', month: '2-digit', year: 'numeric' }).format(date);
}

export function toDateInput(value) {
    if (!value) return '';
    return String(value).slice(0, 10);
}

export function toDateTimeInput(value) {
    const date = value ? new Date(value) : new Date();
    const pad = (part) => String(part).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function imageSrc(base64) {
    if (!base64) return '/images/pet-placeholder.svg';
    if (String(base64).startsWith('data:')) return base64;
    return `data:image/jpeg;base64,${base64}`;
}

export function initials(name = 'Petshop') {
    return name.trim().split(/\s+/).slice(-2).map((part) => part[0]).join('').toUpperCase();
}

export function statusMeta(status = '') {
    const normalized = String(status).trim().toUpperCase();
    const map = {
        ACTIVE: ['Đang bán', 'success'],
        INACTIVE: ['Tạm ẩn', 'neutral'],
        'WAITING CONFIRMATION': ['Chờ xác nhận', 'warning'],
        PENDING: ['Đang chờ', 'warning'],
        CONFIRMED: ['Đã xác nhận', 'info'],
        PROCESSING: ['Đang xử lý', 'info'],
        SHIPPING: ['Đang giao', 'info'],
        DELIVERING: ['Đang giao', 'info'],
        COMPLETED: ['Hoàn tất', 'success'],
        DELIVERED: ['Đã giao', 'success'],
        CANCELLED: ['Đã hủy', 'danger'],
        CANCELED: ['Đã hủy', 'danger'],
        EXPIRED: ['Hết hạn', 'danger'],
        AVAILABLE: ['Còn hiệu lực', 'success'],
        USER: ['Người dùng', 'neutral'],
        CUSTOMER: ['Người dùng', 'neutral'],
        ADMIN: ['Quản trị viên', 'info']
    };
    return map[normalized] || [status || 'Chưa rõ', 'neutral'];
}

export function badge(status) {
    const [label, tone] = statusMeta(status);
    return `<span class="badge badge-${tone}">${escapeHtml(label)}</span>`;
}

export function emptyState(title, description, icon = 'package', action = '') {
    return `<div class="empty-state"><span class="empty-icon" data-icon="${icon}"></span>
        <h3>${escapeHtml(title)}</h3><p>${escapeHtml(description)}</p>${action}</div>`;
}

export function debounce(callback, wait = 250) {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => callback(...args), wait);
    };
}

export function toast(message, type = 'success', title = '') {
    const region = qs('#toast-region');
    if (!region) return;
    const element = document.createElement('div');
    element.className = `toast ${type}`;
    const icon = type === 'error' ? 'triangle-alert' : type === 'warning' ? 'alert-circle' : 'check-circle';
    const heading = title || (type === 'error' ? 'Không thể thực hiện' : type === 'warning' ? 'Cần lưu ý' : 'Thành công');
    element.innerHTML = `<span class="toast-icon" data-icon="${icon}"></span><div><strong>${escapeHtml(heading)}</strong>
        <p>${escapeHtml(message)}</p></div><button class="icon-button" type="button" aria-label="Đóng" data-icon="x"></button>`;
    region.appendChild(element);
    renderIcons(element);
    const remove = () => element.remove();
    qs('button', element).addEventListener('click', remove);
    setTimeout(remove, 4800);
}

export function openModal({ title, eyebrow = 'Petshop', content, size = 'normal' }) {
    const modal = qs('#app-modal');
    qs('#modal-title').textContent = title;
    qs('#modal-eyebrow').textContent = eyebrow;
    qs('#modal-body').innerHTML = content;
    qs('.modal-panel', modal).style.width = size === 'wide' ? 'min(860px, 100%)' : '';
    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    document.body.classList.add('modal-open');
    renderIcons(modal);
    requestAnimationFrame(() => qs('input, select, textarea, button', qs('#modal-body'))?.focus());
}

export function closeModal() {
    const modal = qs('#app-modal');
    if (!modal) return;
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    document.body.classList.remove('modal-open');
    setTimeout(() => { if (!modal.classList.contains('is-open')) qs('#modal-body').innerHTML = ''; }, 180);
}

export function confirmAction({ title = 'Xác nhận thao tác', message, confirmLabel = 'Xác nhận', danger = true }) {
    return new Promise((resolve) => {
        openModal({
            title,
            eyebrow: 'Xác nhận',
            content: `<div class="confirm-content"><span class="confirm-icon" data-icon="${danger ? 'triangle-alert' : 'info'}"></span>
                <p>${escapeHtml(message)}</p><div class="confirm-actions">
                <button class="button button-ghost" type="button" data-confirm="cancel">Quay lại</button>
                <button class="button ${danger ? 'button-danger' : 'button-primary'}" type="button" data-confirm="ok">${escapeHtml(confirmLabel)}</button>
                </div></div>`
        });
        qs('[data-confirm="cancel"]').addEventListener('click', () => { closeModal(); resolve(false); }, { once: true });
        qs('[data-confirm="ok"]').addEventListener('click', () => { closeModal(); resolve(true); }, { once: true });
    });
}

export function formDataObject(form) {
    return Object.fromEntries(new FormData(form).entries());
}

export function setButtonLoading(button, loading, text = 'Đang xử lý...') {
    if (!button) return;
    if (loading) {
        button.dataset.originalHtml = button.innerHTML;
        button.disabled = true;
        button.innerHTML = `<span class="spinner" style="width:16px;height:16px;border-width:2px"></span>${escapeHtml(text)}`;
    } else {
        button.disabled = false;
        if (button.dataset.originalHtml) button.innerHTML = button.dataset.originalHtml;
        renderIcons(button);
    }
}

export function pagination(current, total, onClick) {
    const wrapper = document.createElement('div');
    if (!total || total <= 1) return wrapper;
    const pages = new Set([1, total, current - 1, current, current + 1].filter((page) => page >= 1 && page <= total));
    const add = (label, page, disabled = false, active = false, icon = '') => {
        const button = document.createElement('button');
        button.className = `page-button${active ? ' is-active' : ''}`;
        button.disabled = disabled;
        button.innerHTML = icon ? `<span data-icon="${icon}"></span>` : label;
        button.addEventListener('click', () => onClick(page));
        wrapper.appendChild(button);
    };
    add('', current - 1, current <= 1, false, 'chevron-left');
    let last = 0;
    [...pages].sort((a, b) => a - b).forEach((page) => {
        if (last && page - last > 1) {
            const dots = document.createElement('span');
            dots.textContent = '…';
            dots.style.padding = '0 4px';
            wrapper.appendChild(dots);
        }
        add(String(page), page, false, page === current);
        last = page;
    });
    add('', current + 1, current >= total, false, 'chevron-right');
    renderIcons(wrapper);
    return wrapper;
}

export function humanizeError(error) {
    const messages = {
        'User not exists': 'Email chưa được đăng ký.',
        'Incorrect password': 'Mật khẩu không chính xác.',
        'User already exists': 'Email này đã được sử dụng.',
        'Can not find cart': 'Tài khoản chưa có giỏ hàng trên hệ thống.',
        'Product not found': 'Không tìm thấy sản phẩm.',
        'Category not found': 'Không tìm thấy danh mục.',
        'Size not found': 'Không tìm thấy kích cỡ.',
        'voucher not found': 'Không tìm thấy voucher.',
        'Order not found': 'Không tìm thấy đơn hàng.'
    };
    const message = error?.message || String(error || 'Đã có lỗi xảy ra.');
    return messages[message] || message;
}
