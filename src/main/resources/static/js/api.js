async function request(path, options = {}) {
    const config = { method: options.method || 'GET', headers: { Accept: 'application/json' } };
    if (options.body instanceof FormData || options.body instanceof URLSearchParams) {
        config.body = options.body;
    } else if (options.body !== undefined) {
        config.headers['Content-Type'] = 'application/json';
        config.body = JSON.stringify(options.body);
    }
    const response = await fetch(path, config);
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : { message: await response.text() };
    const businessStatus = payload?.status;
    const failedBusinessStatus = businessStatus && !['OK', 'CREATED', 200, 201].includes(businessStatus);
    if (!response.ok || failedBusinessStatus) {
        const error = new Error(payload?.message || `Yêu cầu thất bại (${response.status})`);
        error.status = response.status;
        error.payload = payload;
        throw error;
    }
    return payload;
}

const page = (path, current = 1) => request(`${path}?page=${current}`);

export const api = {
    login(email, password) {
        return request('/api/login', {
            method: 'POST',
            body: new URLSearchParams({ email, password })
        });
    },
    register(data) { return request('/api/register', { method: 'POST', body: data }); },
    user(id) { return request(`/api/user/id-user=${id}`); },
    users(current) { return page('/api/admin/user', current); },
    addAddress(data) { return request('/api/information-order', { method: 'POST', body: data }); },
    deleteAddress(id) { return request(`/api/information-order/id=${id}`, { method: 'DELETE' }); },
    changePassword(data) { return request('/api/change-password', { method: 'POST', body: data }); },

    products() { return request('/api/product'); },
    adminProducts(current) { return page('/api/admin/product', current); },
    product(id) { return request(`/api/product/id-product=${id}`); },
    productsByCategory(id) { return request(`/api/product/id-cate=${id}`); },
    addProduct(data) { return request('/api/admin/product', { method: 'POST', body: data }); },
    updateProduct(data) { return request('/api/admin/product', { method: 'PUT', body: data }); },
    deleteProduct(id) { return request(`/api/admin/product/id-product=${id}`, { method: 'DELETE' }); },
    addProductSize(data) { return request('/api/admin/product/size', { method: 'POST', body: data }); },
    updateProductSize(data) { return request('/api/admin/product/size', { method: 'PUT', body: data }); },
    deleteProductSize(id) { return request(`/api/admin/product/size/id-size-product=${id}`, { method: 'DELETE' }); },

    categories() { return request('/api/category'); },
    category(id) { return request(`/api/admin/category/id-cate=${id}`); },
    addCategory(data) { return request('/api/admin/category', { method: 'POST', body: data }); },
    updateCategory(data) { return request('/api/admin/category', { method: 'PUT', body: data }); },
    deleteCategory(id) { return request(`/api/admin/category/id-cate=${id}`, { method: 'DELETE' }); },

    sizes() { return request('/api/admin/size'); },
    size(id) { return request(`/api/admin/size/id-size=${id}`); },
    addSize(data) { return request('/api/admin/size', { method: 'POST', body: data }); },
    updateSize(data) { return request('/api/admin/size', { method: 'PUT', body: data }); },
    deleteSize(id) { return request(`/api/admin/size/id-size=${id}`, { method: 'DELETE' }); },

    vouchers() { return request('/api/voucher'); },
    paymentMethods() { return request('/api/payment-method'); },
    adminVouchers(current) { return page('/api/admin/voucher', current); },
    voucher(id) { return request(`/api/admin/voucher/id-voucher=${id}`); },
    addVoucher(data) { return request('/api/admin/voucher', { method: 'POST', body: data }); },
    updateVoucher(data) { return request('/api/admin/voucher', { method: 'PUT', body: data }); },
    deleteVoucher(id) { return request(`/api/admin/voucher/id-voucher=${id}`, { method: 'DELETE' }); },

    imports(current) { return page('/api/admin/import/product', current); },
    importById(id) { return request(`/api/admin/import/product/id=${id}`); },
    importsByProduct(id) { return request(`/api/admin/import/product/id-product=${id}`); },
    addImport(data) { return request('/api/admin/import/product', { method: 'POST', body: data }); },
    updateImport(data) { return request('/api/admin/import/product', { method: 'PUT', body: data }); },
    deleteImport(id) { return request(`/api/admin/import/product/id-import=${id}`, { method: 'DELETE' }); },

    cart(userId) { return request(`/api/cart/id-user=${userId}`); },
    addToCart(data) { return request('/api/cart/item', { method: 'POST', body: data }); },
    removeCartItem(id) { return request(`/api/cart/id-cart-item=${id}`, { method: 'DELETE' }); },
    wishlist(userId) { return request(`/api/wishlist/id-user=${userId}`); },
    addWishlist(data) { return request('/api/wishlist', { method: 'POST', body: data }); },
    deleteWishlist(id) { return request(`/api/wishlist/id-wishlist=${id}`, { method: 'DELETE' }); },

    orders(current) { return page('/api/admin/orders', current); },
    userOrders(userId) { return request(`/api/order/user/id-user=${userId}`); },
    order(id) { return request(`/api/order/id-order=${id}`); },
    createOrder(data) { return request('/api/order', { method: 'POST', body: data }); },
    updateOrderStatus(idOrder, status) {
        return request(`/api/order/status?idOrder=${idOrder}&status=${encodeURIComponent(status)}`, { method: 'PUT' });
    },
    deleteOrder(id) { return request(`/api/order/id-order=${id}`, { method: 'DELETE' }); }
};
