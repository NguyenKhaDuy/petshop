import { api } from '../api.js';
import {
    qs, qsa, renderIcons, formatCurrency, formatDate, imageSrc, escapeHtml, badge, emptyState,
    toast, humanizeError, confirmAction, openModal, closeModal, formDataObject, setButtonLoading, initials
} from '../utils.js';
import {
    state, loadProducts, loadCustomerCollections, loadCurrentUser
} from '../state.js';

function loginRequiredState() {
    return emptyState('Vui lòng đăng nhập', 'Đăng nhập để sử dụng chức năng này.', 'user',
        '<a class="button button-primary" href="#/login">Đăng nhập</a>');
}

export async function renderCart({ root, refreshShell }) {
    if (!state.session) {
        qs('[data-role="cart-items"]', root).innerHTML = loginRequiredState();
        return;
    }
    await loadCustomerCollections(true);
    const products = await loadProducts();
    const items = state.cart?.cartItems || [];
    const list = qs('[data-role="cart-items"]', root);
    const count = items.reduce((sum, item) => sum + Number(item.quantity || 0), 0);
    const selectedCartItemIds = new Set(items.map((item) => String(item.idCartItem)));
    qs('[data-role="cart-items-count"]', root).textContent = `${count} món`;

    const draw = () => {
        list.innerHTML = items.map((item) => {
            const product = products.find((entry) => Number(entry.idProduct) === Number(item.idProduct));
            return `<article class="cart-item is-selected" data-cart-row="${item.idCartItem}">
                <label class="cart-item-select" title="Chọn sản phẩm này">
                    <input type="checkbox" data-select-cart="${item.idCartItem}" checked>
                    <span class="sr-only">Chọn ${escapeHtml(item.productName)}</span>
                </label>
                <a class="cart-item-image" href="#/product/${item.idProduct}"><img src="${imageSrc(product?.imageDTOS?.[0]?.imageBase64)}"
                    alt="${escapeHtml(item.productName)}"></a>
                <div class="cart-item-info"><h3><a href="#/product/${item.idProduct}">${escapeHtml(item.productName)}</a></h3>
                    <span>Kích cỡ: ${escapeHtml(item.size || '—')} · Số lượng: ${item.quantity}</span></div>
                <strong class="cart-item-price">${formatCurrency(item.totalPrice)}</strong>
                <button class="icon-button" type="button" data-remove-cart="${item.idCartItem}" aria-label="Xóa"
                    data-icon="trash"></button></article>`;
        }).join('') || emptyState('Giỏ hàng đang trống', 'Hãy chọn vài món đồ thật xinh cho thú cưng.', 'shopping-bag',
            '<a class="button button-primary" href="#/products">Khám phá sản phẩm</a>');
        renderIcons(list);
    };
    draw();

    const drawSummary = () => {
        const selectedItems = items.filter((item) => selectedCartItemIds.has(String(item.idCartItem)));
        const selectedCount = selectedItems.reduce((sum, item) => sum + Number(item.quantity || 0), 0);
        const selectedTotal = selectedItems.reduce((sum, item) => sum + Number(item.totalPrice || 0), 0);
        const allSelected = items.length > 0 && selectedItems.length === items.length;
        const summary = qs('[data-role="cart-summary"]', root);
        summary.innerHTML = `<h2>Tóm tắt đơn hàng</h2>
            <label class="cart-select-all">
                <input type="checkbox" data-select-all ${allSelected ? 'checked' : ''}>
                <span>Chọn tất cả (${items.length} sản phẩm)</span>
            </label>
            <div class="summary-row"><span>Đã chọn (${selectedCount} món)</span><strong>${formatCurrency(selectedTotal)}</strong></div>
            <div class="summary-row"><span>Phí vận chuyển</span><strong>Miễn phí</strong></div>
            <div class="summary-total"><span>Tạm tính</span><strong>${formatCurrency(selectedTotal)}</strong></div>
            <button class="button button-primary button-block" data-action="checkout"
                ${selectedItems.length ? '' : 'disabled'}>
                Đặt ${selectedItems.length} sản phẩm <span data-icon="arrow-right"></span></button>
            <div class="summary-note"><span data-icon="shield-check"></span>
                <span>Chỉ những sản phẩm đã chọn mới được đưa vào đơn hàng.</span></div>`;

        const selectAll = qs('[data-select-all]', summary);
        if (selectAll) {
            selectAll.indeterminate = selectedItems.length > 0 && !allSelected;
            selectAll.addEventListener('change', () => {
                selectedCartItemIds.clear();
                if (selectAll.checked) {
                    items.forEach((item) => selectedCartItemIds.add(String(item.idCartItem)));
                }
                qsa('[data-select-cart]', list).forEach((input) => {
                    input.checked = selectAll.checked;
                    qs(`[data-cart-row="${input.dataset.selectCart}"]`, list)
                        ?.classList.toggle('is-selected', input.checked);
                });
                drawSummary();
            });
        }
        qs('[data-action="checkout"]', summary)?.addEventListener('click', () =>
            openCheckout(selectedItems, selectedTotal, refreshShell));
        renderIcons(summary);
    };

    qsa('[data-select-cart]', list).forEach((input) => input.addEventListener('change', () => {
        const id = String(input.dataset.selectCart);
        if (input.checked) selectedCartItemIds.add(id);
        else selectedCartItemIds.delete(id);
        qs(`[data-cart-row="${id}"]`, list)?.classList.toggle('is-selected', input.checked);
        drawSummary();
    }));
    drawSummary();

    qsa('[data-remove-cart]', list).forEach((button) => button.addEventListener('click', async () => {
        const confirmed = await confirmAction({
            title: 'Xóa khỏi giỏ hàng?',
            message: 'Sản phẩm sẽ được loại khỏi giỏ hiện tại.',
            confirmLabel: 'Xóa sản phẩm'
        });
        if (!confirmed) return;
        try {
            await api.removeCartItem(button.dataset.removeCart);
            toast('Đã xóa sản phẩm khỏi giỏ.');
            await loadCustomerCollections(true);
            await refreshShell();
            await renderCart({ root, refreshShell });
        } catch (error) {
            toast(humanizeError(error), 'error');
        }
    }));
    renderIcons(root);
}

async function openCheckout(items, total, refreshShell) {
    try {
        const [user, paymentResponse, voucherResponse] = await Promise.all([
            loadCurrentUser(true),
            api.paymentMethods(),
            api.vouchers()
        ]);
        const addresses = user?.informationOrderDTOS || [];
        const paymentMethods = paymentResponse.data || [];
        const vouchers = voucherResponse.data || [];

        if (!addresses.length) {
            toast('Vui lòng thêm địa chỉ nhận hàng trong hồ sơ trước khi đặt hàng.', 'warning');
            location.hash = '#/profile';
            return;
        }
        if (!paymentMethods.length) {
            toast('Hệ thống chưa cấu hình phương thức thanh toán.', 'warning');
            return;
        }

        openModal({
            title: 'Xác nhận đặt hàng',
            eyebrow: 'Thanh toán',
            size: 'wide',
            content: `<form data-checkout-form class="form-grid">
                <label class="form-field form-span-2"><span>Địa chỉ nhận hàng</span>
                    <select name="idInformationOrder" required>
                        ${addresses.map((address) => `<option value="${address.idInformationOrder}">
                            ${escapeHtml(address.nameOrder)} — ${escapeHtml(address.phoneOrder)} — ${escapeHtml(address.addressOrder)}
                        </option>`).join('')}
                    </select>
                </label>
                <label class="form-field"><span>Phương thức thanh toán</span>
                    <select name="idPaymentMethod" required>
                        ${paymentMethods.map((method) => `<option value="${method.idPaymentMethod}">
                            ${escapeHtml(method.method)}
                        </option>`).join('')}
                    </select>
                </label>
                <label class="form-field"><span>Voucher</span>
                    <select name="idVoucher">
                        <option value="">Không sử dụng voucher</option>
                        ${vouchers.map((voucher) => `<option value="${voucher.idVoucher}" data-discount="${voucher.discount}">
                            ${escapeHtml(voucher.code)} — giảm ${voucher.discount}% — còn ${voucher.quantity}
                        </option>`).join('')}
                    </select>
                </label>
                <label class="form-field form-span-2"><span>Ghi chú</span>
                    <textarea name="note" placeholder="Ghi chú cho đơn hàng (không bắt buộc)"></textarea>
                </label>
                <div class="checkout-totals form-span-2">
                    <div class="summary-row"><span>Tạm tính</span><strong>${formatCurrency(total)}</strong></div>
                    <div class="summary-row"><span>Voucher</span><strong data-checkout-discount>− ${formatCurrency(0)}</strong></div>
                    <div class="summary-total"><span>Tổng thanh toán</span>
                        <strong data-checkout-total>${formatCurrency(total)}</strong></div>
                </div>
                <div class="form-actions form-span-2">
                    <button class="button button-ghost" type="button" data-modal-cancel>Quay lại</button>
                    <button class="button button-primary" type="submit">Xác nhận đặt hàng</button>
                </div>
            </form>`
        });

        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-checkout-form]');
        const voucherSelect = qs('select[name="idVoucher"]', form);
        voucherSelect.addEventListener('change', () => {
            const discountPercent = Number(voucherSelect.selectedOptions[0]?.dataset.discount || 0);
            const discountAmount = total * discountPercent / 100;
            qs('[data-checkout-discount]', form).textContent = `− ${formatCurrency(discountAmount)}`;
            qs('[data-checkout-total]', form).textContent = formatCurrency(total - discountAmount);
        });
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const submitButton = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            setButtonLoading(submitButton, true, 'Đang tạo đơn...');
            try {
                await api.createOrder({
                    idUser: state.session.idUser,
                    note: data.note?.trim() || null,
                    idPaymentMethod: Number(data.idPaymentMethod),
                    idInformationOrder: Number(data.idInformationOrder),
                    orderItemRequests: items.map((item) => ({
                        idProduct: Number(item.idProduct),
                        idSize: Number(item.idSize),
                        quantity: Number(item.quantity)
                    })),
                    idVoucher: data.idVoucher ? Number(data.idVoucher) : null
                });

                const clearResults = await Promise.allSettled(
                    items.map((item) => api.removeCartItem(item.idCartItem))
                );
                state.cart = null;
                await loadCustomerCollections(true);
                await refreshShell();
                closeModal();
                if (clearResults.some((result) => result.status === 'rejected')) {
                    toast('Đơn hàng đã tạo nhưng một số sản phẩm chưa được xóa khỏi giỏ.', 'warning');
                } else {
                    toast('Đơn hàng đã được tạo thành công.');
                }
                location.hash = '#/orders';
            } catch (error) {
                toast(humanizeError(error), 'error', 'Chưa thể tạo đơn hàng');
                setButtonLoading(submitButton, false);
            }
        });
    } catch (error) {
        toast(humanizeError(error), 'error', 'Chưa thể mở thanh toán');
    }
}

export async function renderWishlist({ root, refreshShell }) {
    if (!state.session) {
        qs('[data-role="wishlist-grid"]', root).innerHTML = loginRequiredState();
        return;
    }
    await loadCustomerCollections(true);
    const grid = qs('[data-role="wishlist-grid"]', root);
    const draw = () => {
        grid.innerHTML = state.wishlist.map((item) => `<article class="product-card">
            <a class="product-card-media" href="#/product/${item.idProduct}">
                <img src="${imageSrc(item.imageProduct)}" alt="${escapeHtml(item.productName)}"></a>
            <button class="icon-button wishlist-button is-active" data-remove-wishlist="${item.idWishlist}"
                type="button" aria-label="Bỏ yêu thích" data-icon="heart"></button>
            <div class="product-card-body"><span class="product-category">Đã lưu</span>
                <h3><a href="#/product/${item.idProduct}">${escapeHtml(item.productName)}</a></h3>
                <div class="product-card-footer"><span class="table-secondary">${formatDate(item.createdAt)}</span>
                    <a class="mini-action" href="#/product/${item.idProduct}" data-icon="arrow-right"></a></div></div>
            </article>`).join('') || emptyState('Chưa có sản phẩm yêu thích',
                'Chạm vào biểu tượng trái tim để lưu sản phẩm bạn quan tâm.', 'heart',
                '<a class="button button-primary" href="#/products">Xem sản phẩm</a>');
        renderIcons(grid);
        qsa('[data-remove-wishlist]', grid).forEach((button) => button.addEventListener('click', async () => {
            try {
                await api.deleteWishlist(button.dataset.removeWishlist);
                await loadCustomerCollections(true);
                await refreshShell();
                toast('Đã bỏ sản phẩm khỏi danh sách yêu thích.');
                draw();
            } catch (error) { toast(humanizeError(error), 'error'); }
        }));
    };
    draw();
}

function orderDetailContent(order) {
    const info = order.informationOrderDTO || {};
    return `<div class="detail-list">
        <div class="detail-item"><span>Mã đơn hàng</span><strong>#${order.idOrder}</strong></div>
        <div class="detail-item"><span>Trạng thái</span><strong>${badge(order.status)}</strong></div>
        <div class="detail-item"><span>Người nhận</span><strong>${escapeHtml(info.nameOrder || order.nameUser || '—')}</strong></div>
        <div class="detail-item"><span>Số điện thoại</span><strong>${escapeHtml(info.phoneOrder || '—')}</strong></div>
        <div class="detail-item"><span>Địa chỉ</span><strong>${escapeHtml(info.addressOrder || '—')}</strong></div>
        <div class="detail-item"><span>Thanh toán</span><strong>${escapeHtml(order.paymentMethod || '—')}</strong></div>
        <div class="detail-item"><span>Voucher</span><strong>${escapeHtml(order.voucherCode || 'Không áp dụng')}</strong></div>
        <div class="detail-item"><span>Tổng tiền</span><strong>${formatCurrency(order.totalAmount)}</strong></div>
        </div>
        <div class="modal-section"><h3>Sản phẩm</h3>${(order.orderDetailDTOS || []).map((item) =>
            `<div class="order-detail-item"><img src="${imageSrc(item.imageProduct)}" alt="">
                <div><strong>${escapeHtml(item.nameProduct)}</strong><span>Kích cỡ ${escapeHtml(item.size)} · SL ${item.quantity}</span></div>
                <strong>${formatCurrency(item.totalPrice)}</strong></div>`).join('') ||
            '<p class="table-secondary">Không có dữ liệu sản phẩm.</p>'}</div>
        ${order.note ? `<div class="modal-section"><h3>Ghi chú</h3><p>${escapeHtml(order.note)}</p></div>` : ''}`;
}

async function showOrder(id) {
    try {
        const response = await api.order(id);
        openModal({ title: `Chi tiết đơn #${id}`, eyebrow: 'Đơn hàng', content: orderDetailContent(response.data), size: 'wide' });
    } catch (error) { toast(humanizeError(error), 'error'); }
}

export async function renderCustomerOrders({ root }) {
    if (!state.session) {
        qs('[data-role="customer-order-list"]', root).innerHTML = loginRequiredState();
        return;
    }
    const response = await api.userOrders(state.session.idUser);
    const orders = response.data || [];
    const list = qs('[data-role="customer-order-list"]', root);
    let filter = '';
    const draw = () => {
        const filtered = orders.filter((order) => !filter || order.status === filter);
        list.innerHTML = filtered.map((order) => `<article class="order-card">
            <div class="order-card-header"><div><strong>Đơn hàng #${order.idOrder}</strong>
                <span>${formatDate(order.createdAt, true)}</span></div>${badge(order.status)}</div>
            <div class="order-card-body"><div class="order-card-products">${(order.orderDetailDTOS || []).slice(0, 4).map((item) =>
                `<span class="order-product-thumb"><img src="${imageSrc(item.imageProduct)}" alt="${escapeHtml(item.nameProduct)}"></span>`).join('')}
                <span class="table-secondary" style="margin-left:16px">${order.orderDetailDTOS?.length || 0} sản phẩm</span></div>
                <div class="order-total"><span>Tổng thanh toán</span><strong>${formatCurrency(order.totalAmount)}</strong></div></div>
            <div class="order-card-actions"><button class="button button-ghost button-small" data-view-order="${order.idOrder}">Xem chi tiết</button>
                ${String(order.status).toUpperCase() === 'WAITING CONFIRMATION'
                    ? `<button class="button button-ghost button-small" data-cancel-order="${order.idOrder}">Hủy đơn</button>` : ''}</div>
            </article>`).join('') || emptyState('Không có đơn hàng', filter
                ? 'Không có đơn hàng ở trạng thái này.' : 'Các đơn hàng của bạn sẽ xuất hiện tại đây.', 'clipboard-list');
        qsa('[data-view-order]', list).forEach((button) => button.addEventListener('click', () => showOrder(button.dataset.viewOrder)));
        qsa('[data-cancel-order]', list).forEach((button) => button.addEventListener('click', async () => {
            const confirmed = await confirmAction({
                title: 'Hủy đơn hàng?',
                message: `Bạn có chắc muốn hủy đơn #${button.dataset.cancelOrder}?`,
                confirmLabel: 'Hủy đơn hàng'
            });
            if (!confirmed) return;
            try {
                await api.updateOrderStatus(button.dataset.cancelOrder, 'CANCELLED');
                const order = orders.find((entry) => String(entry.idOrder) === button.dataset.cancelOrder);
                if (order) order.status = 'CANCELLED';
                toast('Đơn hàng đã được hủy.');
                draw();
            } catch (error) { toast(humanizeError(error), 'error'); }
        }));
        renderIcons(list);
    };
    qsa('[data-status]', root).forEach((button) => button.addEventListener('click', () => {
        qsa('[data-status]', root).forEach((item) => item.classList.toggle('is-active', item === button));
        filter = button.dataset.status;
        draw();
    }));
    draw();
}

function addressForm() {
    return `<form data-modal-form class="form-grid">
        <label class="form-field"><span>Tên người nhận</span><input name="nameOrder" required placeholder="Nguyễn Minh Anh"></label>
        <label class="form-field"><span>Số điện thoại</span><input name="phoneOrder" type="tel" required placeholder="09xx xxx xxx"></label>
        <label class="form-field form-span-2"><span>Địa chỉ đầy đủ</span><textarea name="addressOrder" required
            placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố"></textarea></label>
        <div class="form-actions form-span-2"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">Lưu địa chỉ</button></div></form>`;
}

export async function renderProfile({ root }) {
    const user = await loadCurrentUser(true);
    if (!user) return;
    qs('[data-role="profile-overview"]', root).innerHTML = `<span class="profile-avatar">${initials(user.name)}</span>
        <span class="eyebrow">${escapeHtml(user.role || 'USER')}</span><h2>${escapeHtml(user.name)}</h2>
        <p>Thành viên từ ${formatDate(user.createdAt)}</p>
        <div class="profile-detail"><div><span data-icon="mail"></span>${escapeHtml(user.email)}</div>
            <div><span data-icon="shield-check"></span>${badge(user.role)}</div></div>`;
    const drawAddresses = () => {
        const list = qs('[data-role="address-list"]', root);
        list.innerHTML = (state.user.informationOrderDTOS || []).map((address, index) => `<article class="address-card">
            <span class="badge badge-${index === 0 ? 'success' : 'neutral'}">${index === 0 ? 'Địa chỉ chính' : `Địa chỉ ${index + 1}`}</span>
            <h3>${escapeHtml(address.nameOrder)}</h3><p>${escapeHtml(address.addressOrder)}</p>
            <small>${escapeHtml(address.phoneOrder)}</small>
            <button class="icon-button" data-delete-address="${address.idInformationOrder}" aria-label="Xóa địa chỉ" data-icon="trash"></button>
            </article>`).join('') || emptyState('Chưa có địa chỉ', 'Thêm địa chỉ để chuẩn bị cho việc đặt hàng.', 'map-pin');
        qsa('[data-delete-address]', list).forEach((button) => button.addEventListener('click', async () => {
            const confirmed = await confirmAction({
                title: 'Xóa địa chỉ?',
                message: 'Địa chỉ này sẽ bị xóa khỏi sổ địa chỉ.',
                confirmLabel: 'Xóa địa chỉ'
            });
            if (!confirmed) return;
            try {
                await api.deleteAddress(button.dataset.deleteAddress);
                await loadCurrentUser(true);
                toast('Đã xóa địa chỉ.');
                drawAddresses();
            } catch (error) { toast(humanizeError(error), 'error'); }
        }));
        renderIcons(list);
    };
    drawAddresses();
    qs('[data-action="add-address"]', root).addEventListener('click', () => {
        openModal({ title: 'Thêm địa chỉ nhận hàng', eyebrow: 'Sổ địa chỉ', content: addressForm() });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                await api.addAddress({ idUser: state.session.idUser, ...data });
                await loadCurrentUser(true);
                closeModal();
                toast('Đã thêm địa chỉ mới.');
                drawAddresses();
            } catch (error) {
                toast(humanizeError(error), 'error');
                setButtonLoading(button, false);
            }
        });
    });
    const passwordForm = qs('[data-role="password-form"]', root);
    passwordForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!passwordForm.reportValidity()) return;
        const button = qs('button[type="submit"]', passwordForm);
        const data = formDataObject(passwordForm);
        setButtonLoading(button, true, 'Đang cập nhật...');
        try {
            await api.changePassword({ idUser: state.session.idUser, ...data });
            passwordForm.reset();
            toast('Mật khẩu đã được cập nhật.');
        } catch (error) { toast(humanizeError(error), 'error'); }
        finally { setButtonLoading(button, false); }
    });
    renderIcons(root);
}
