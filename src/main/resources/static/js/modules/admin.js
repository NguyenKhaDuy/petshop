import { api } from '../api.js';
import {
    qs, qsa, renderIcons, formatCurrency, formatDate, toDateInput, toDateTimeInput, imageSrc,
    escapeHtml, badge, emptyState, debounce, toast, humanizeError, confirmAction, openModal,
    closeModal, formDataObject, setButtonLoading, pagination, initials, ratingStars
} from '../utils.js';
import {
    state, loadProducts, loadCategories, loadCurrentUser
} from '../state.js';

const PAGE_SIZE = 20;

function totalStock(product) {
    return (product.sizeProductDTOS || []).reduce((sum, item) => sum + Number(item.quantity || 0), 0);
}

function minPrice(product) {
    const values = (product.sizeProductDTOS || []).map((item) => Number(item.price)).filter(Number.isFinite);
    return values.length ? Math.min(...values) : 0;
}

function bindPagination(container, response, loadPage) {
    container.innerHTML = '';
    const control = pagination(Number(response.currentPage || 1), Number(response.totalPages || 1), loadPage);
    container.append(...control.childNodes);
}

function actionButtons(id, actions = ['view', 'edit', 'delete']) {
    const config = {
        view: ['eye', 'Xem'],
        edit: ['edit', 'Sửa'],
        delete: ['trash', 'Xóa']
    };
    return `<div class="table-actions">${actions.map((action) =>
        `<button class="icon-button${action === 'delete' ? ' danger' : ''}" type="button" data-row-action="${action}"
            data-id="${id}" aria-label="${config[action][1]}" title="${config[action][1]}" data-icon="${config[action][0]}"></button>`).join('')}</div>`;
}

export async function renderAdminDashboard({ root }) {
    const user = await loadCurrentUser();
    qs('[data-role="admin-greeting"]', root).textContent = `Xin chào ${user?.name || 'quản trị viên'}, đây là dữ liệu mới nhất từ hệ thống.`;
    qs('[data-role="current-date"]', root).textContent = new Intl.DateTimeFormat('vi-VN', {
        weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric'
    }).format(new Date());
    const [products, categories, vouchersResponse, ordersResponse, usersResponse] = await Promise.all([
        loadProducts(true), loadCategories(true), api.vouchers(), api.orders(1), api.users(1)
    ]);
    const vouchers = vouchersResponse.data || [];
    const orders = ordersResponse.data || [];
    const users = usersResponse.data || [];
    const stats = [
        ['Sản phẩm', products.length, 'package', `${products.filter((item) => totalStock(item) > 0).length} đang có hàng`],
        ['Danh mục', categories.length, 'tags', 'Nhóm sản phẩm đang dùng'],
        ['Đơn trang này', orders.length, 'clipboard-list', `${orders.filter((item) => item.status === 'WAITING CONFIRMATION').length} chờ xác nhận`],
        ['Khách trang này', users.length, 'users', `${vouchers.length} voucher công khai`]
    ];
    qs('[data-role="admin-stats"]', root).innerHTML = stats.map(([label, value, icon, note]) => `<article class="stat-card">
        <div class="stat-card-top"><span class="stat-icon" data-icon="${icon}"></span><span class="stat-trend">${escapeHtml(note)}</span></div>
        <div><small>${escapeHtml(label)}</small><strong>${value}</strong></div></article>`).join('');
    qs('[data-role="recent-orders"]', root).innerHTML = orders.slice(0, 6).map((order) => `<tr>
        <td><strong class="table-primary">#${order.idOrder}</strong><span class="table-secondary">${formatDate(order.createdAt, true)}</span></td>
        <td>${escapeHtml(order.nameUser || '—')}</td><td><strong>${formatCurrency(order.totalAmount)}</strong></td>
        <td>${badge(order.status)}</td></tr>`).join('') ||
        `<tr><td colspan="4">${emptyState('Chưa có đơn hàng', 'Đơn hàng mới sẽ xuất hiện tại đây.', 'clipboard-list')}</td></tr>`;
    const lowStock = products.flatMap((product) => (product.sizeProductDTOS || []).map((variant) => ({
        product, variant
    }))).filter(({ variant }) => Number(variant.quantity) <= 5).sort((a, b) => a.variant.quantity - b.variant.quantity).slice(0, 7);
    qs('[data-role="low-stock-list"]', root).innerHTML = lowStock.map(({ product, variant }) => `<div class="stock-item">
        <span class="stock-item-icon" data-icon="box"></span><div class="stock-item-copy">
            <strong>${escapeHtml(product.nameProduct)}</strong><span>Kích cỡ ${escapeHtml(variant.size)}</span></div>
        <strong>${variant.quantity} còn lại</strong></div>`).join('') ||
        emptyState('Tồn kho ổn định', 'Không có biến thể nào dưới ngưỡng 5 sản phẩm.', 'check-circle');
    qsa('[data-quick-action]', root).forEach((button) => button.addEventListener('click', () => {
        const route = { product: '#/admin/products', import: '#/admin/imports', voucher: '#/admin/vouchers' }[button.dataset.quickAction];
        sessionStorage.setItem('pawcare.quickAction', button.dataset.quickAction);
        location.hash = route;
    }));
    renderIcons(root);
}

function productForm(product, categories) {
    return `<form data-modal-form enctype="multipart/form-data" class="form-grid">
        ${product ? `<input type="hidden" name="idProduct" value="${product.idProduct}">` : ''}
        <label class="form-field form-span-2"><span>Tên sản phẩm</span><input name="nameProduct" required minlength="2"
            value="${escapeHtml(product?.nameProduct || '')}" placeholder="Ví dụ: Hạt dinh dưỡng cho mèo"></label>
        <label class="form-field"><span>Danh mục</span><select name="idCategory" required><option value="">Chọn danh mục</option>
            ${categories.map((category) => `<option value="${category.idCategory}" ${Number(product?.idCategory) === Number(category.idCategory) ? 'selected' : ''}>
                ${escapeHtml(category.nameCategory)}</option>`).join('')}</select></label>
        <div class="form-field form-span-2"><span>Hình ảnh sản phẩm</span>
            <label class="product-image-upload">
                <input data-product-image-input name="images" type="file"
                    accept="image/png,image/jpeg,image/webp" multiple>
                <span class="product-image-upload-icon" data-icon="plus"></span>
                <strong>Chọn nhiều ảnh</strong>
                <small>PNG, JPG hoặc WebP · Có thể chọn bổ sung nhiều lần</small>
            </label>
            <div class="product-image-preview" data-product-image-preview></div>
            <small class="field-help">Nhấn dấu × trên từng ảnh để loại bỏ trước khi lưu.</small>
        </div>
        <label class="form-field form-span-2"><span>Mô tả</span><textarea name="description" required
            placeholder="Mô tả công dụng, đối tượng phù hợp...">${escapeHtml(product?.description || '')}</textarea></label>
        <div class="form-actions form-span-2"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${product ? 'Lưu thay đổi' : 'Tạo sản phẩm'}</button></div>
        </form>`;
}

function readImageFile(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

function bindProductImagePicker(form, product) {
    const input = qs('[data-product-image-input]', form);
    const preview = qs('[data-product-image-preview]', form);
    let sequence = 0;
    let retainedImages = (product?.imageDTOS || []).map((image) => ({
        key: `existing-${image.idImage}`,
        idImage: Number(image.idImage),
        src: imageSrc(image.imageBase64),
        type: 'existing'
    }));
    let selectedImages = [];

    const render = () => {
        const images = [...retainedImages, ...selectedImages];
        preview.innerHTML = images.map((image, index) => `<article class="product-image-preview-item">
            <img src="${image.src}" alt="Ảnh sản phẩm ${index + 1}">
            <span class="product-image-kind">${image.type === 'existing' ? 'Hiện có' : 'Mới'}</span>
            <button type="button" data-remove-product-image="${image.key}"
                aria-label="Loại bỏ ảnh ${index + 1}" title="Loại bỏ ảnh">×</button>
        </article>`).join('') || `<div class="product-image-preview-empty">
            <span data-icon="image"></span><span>Chưa có ảnh nào được chọn</span>
        </div>`;

        qsa('[data-remove-product-image]', preview).forEach((button) => {
            button.addEventListener('click', () => {
                retainedImages = retainedImages.filter((image) => image.key !== button.dataset.removeProductImage);
                selectedImages = selectedImages.filter((image) => image.key !== button.dataset.removeProductImage);
                render();
            });
        });
        renderIcons(preview);
    };

    input.addEventListener('change', async () => {
        try {
            const files = [...input.files].filter((file) => file.type.startsWith('image/'));
            const knownFiles = new Set(selectedImages.map((image) =>
                `${image.file.name}-${image.file.size}-${image.file.lastModified}`));
            const additions = [];
            for (const file of files) {
                const signature = `${file.name}-${file.size}-${file.lastModified}`;
                if (knownFiles.has(signature)) continue;
                knownFiles.add(signature);
                additions.push({
                    key: `new-${Date.now()}-${sequence++}`,
                    file,
                    src: await readImageFile(file),
                    type: 'new'
                });
            }
            selectedImages.push(...additions);
            render();
        } catch {
            toast('Không thể đọc một trong các ảnh đã chọn.', 'error');
        } finally {
            input.value = '';
        }
    });

    render();

    return {
        formData() {
            const data = new FormData(form);
            data.delete('images');
            selectedImages.forEach((image) => data.append('images', image.file));
            if (product) {
                data.append('imageSelectionProvided', 'true');
                retainedImages.forEach((image) => data.append('retainedImageIds', image.idImage));
            }
            return data;
        }
    };
}

function openProductForm(product, categories, onSaved) {
    openModal({
        title: product ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm',
        eyebrow: 'Kho sản phẩm',
        content: productForm(product, categories),
        size: 'wide'
    });
    const form = qs('[data-modal-form]');
    const imagePicker = bindProductImagePicker(form, product);
    qs('[data-modal-cancel]').addEventListener('click', closeModal);
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const button = qs('button[type="submit"]', form);
        const data = imagePicker.formData();
        setButtonLoading(button, true, 'Đang lưu...');
        try {
            if (product) await api.updateProduct(data);
            else await api.addProduct(data);
            await loadProducts(true);
            closeModal();
            toast(product ? 'Đã cập nhật sản phẩm.' : 'Đã thêm sản phẩm mới.');
            await onSaved();
        } catch (error) {
            toast(humanizeError(error), 'error');
            setButtonLoading(button, false);
        }
    });
}

function variantForm(product, sizes, variant = null) {
    const selectedSize = sizes.find((size) => String(size.size).toLowerCase() === String(variant?.size || '').toLowerCase());
    return `<form data-variant-form class="form-grid">
        <label class="form-field"><span>Kích cỡ</span><select name="idSize" required ${variant ? 'disabled' : ''}>
            <option value="">Chọn kích cỡ</option>${sizes.map((size) =>
                `<option value="${size.idSize}" ${Number(selectedSize?.idSize) === Number(size.idSize) ? 'selected' : ''}>${escapeHtml(size.size)}</option>`).join('')}
            </select>${variant ? `<input type="hidden" name="idSize" value="${selectedSize?.idSize || ''}">` : ''}</label>
        <label class="form-field"><span>Giá bán</span><input name="price" type="number" min="0" step="1000"
            value="${variant?.price ?? ''}" placeholder="150000" required></label>
        <label class="form-field"><span>Số lượng tồn</span><input name="quantity" type="number" min="0"
            value="${variant?.quantity ?? 0}" required></label>
        <div class="form-actions form-span-2"><button class="button button-ghost" type="button" data-variant-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${variant ? 'Cập nhật' : 'Thêm biến thể'}</button></div>
        </form>`;
}

async function showProductDetail(productId, onChanged) {
    const [response, sizesResponse] = await Promise.all([api.product(productId), api.sizes()]);
    let product = response.data;
    const sizes = sizesResponse.data || [];
    const renderContent = () => `<div class="detail-list">
        <div class="detail-item"><span>Mã sản phẩm</span><strong>#${product.idProduct}</strong></div>
        <div class="detail-item"><span>Danh mục</span><strong>${escapeHtml(product.categoryName)}</strong></div>
        <div class="detail-item"><span>Trạng thái</span><strong>${badge(product.status)}</strong></div>
        <div class="detail-item"><span>Tổng tồn</span><strong>${totalStock(product)}</strong></div></div>
        <div class="modal-section"><h3>${escapeHtml(product.nameProduct)}</h3><p>${escapeHtml(product.description || 'Chưa có mô tả.')}</p></div>
        <div class="modal-section"><div class="panel-heading" style="padding:0 0 10px;min-height:auto">
            <h3>Biến thể kích cỡ</h3><button class="button button-primary button-small" data-add-variant>
                <span data-icon="plus"></span> Thêm biến thể</button></div>
            <div class="variant-list">${(product.sizeProductDTOS || []).map((variant) => `<div class="variant-row">
                <strong>Kích cỡ ${escapeHtml(variant.size)}</strong><span>${formatCurrency(variant.price)}</span>
                <span>${variant.quantity} trong kho</span><div class="variant-actions">
                <button class="icon-button" data-edit-variant="${variant.idSizeProduct}" data-icon="edit" aria-label="Sửa"></button>
                <button class="icon-button danger" data-delete-variant="${variant.idSizeProduct}" data-icon="trash" aria-label="Xóa"></button>
                </div></div>`).join('') || '<p class="table-secondary">Chưa có biến thể.</p>'}</div></div>`;
    const bind = () => {
        renderIcons(qs('#app-modal'));
        qs('[data-add-variant]')?.addEventListener('click', () => openVariant(null));
        qsa('[data-edit-variant]').forEach((button) => button.addEventListener('click', () =>
            openVariant(product.sizeProductDTOS.find((item) => String(item.idSizeProduct) === button.dataset.editVariant))));
        qsa('[data-delete-variant]').forEach((button) => button.addEventListener('click', async () => {
            const confirmed = await confirmAction({
                title: 'Xóa biến thể?',
                message: 'Biến thể kích cỡ này sẽ bị xóa khỏi sản phẩm.',
                confirmLabel: 'Xóa biến thể'
            });
            if (!confirmed) return;
            try {
                await api.deleteProductSize(button.dataset.deleteVariant);
                product = (await api.product(productId)).data;
                openModal({ title: 'Chi tiết sản phẩm', eyebrow: 'Sản phẩm', content: renderContent(), size: 'wide' });
                bind();
                await loadProducts(true);
                await onChanged();
                toast('Đã xóa biến thể.');
            } catch (error) { toast(humanizeError(error), 'error'); }
        }));
    };
    const openVariant = (variant) => {
        openModal({
            title: variant ? 'Cập nhật biến thể' : 'Thêm biến thể',
            eyebrow: product.nameProduct,
            content: variantForm(product, sizes, variant)
        });
        qs('[data-variant-cancel]').addEventListener('click', () => {
            openModal({ title: 'Chi tiết sản phẩm', eyebrow: 'Sản phẩm', content: renderContent(), size: 'wide' });
            bind();
        });
        const form = qs('[data-variant-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            const payload = {
                idSizeProduct: variant?.idSizeProduct || null,
                idProduct: product.idProduct,
                idSize: Number(data.idSize),
                price: Number(data.price),
                quantity: Number(data.quantity)
            };
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                if (variant) await api.updateProductSize(payload);
                else await api.addProductSize(payload);
                product = (await api.product(productId)).data;
                await loadProducts(true);
                await onChanged();
                openModal({ title: 'Chi tiết sản phẩm', eyebrow: 'Sản phẩm', content: renderContent(), size: 'wide' });
                bind();
                toast(variant ? 'Đã cập nhật biến thể.' : 'Đã thêm biến thể.');
            } catch (error) {
                toast(humanizeError(error), 'error');
                setButtonLoading(button, false);
            }
        });
    };
    openModal({ title: 'Chi tiết sản phẩm', eyebrow: 'Sản phẩm', content: renderContent(), size: 'wide' });
    bind();
}

export async function renderAdminProducts({ root }) {
    const categories = await loadCategories(true);
    const table = qs('[data-role="admin-product-table"]', root);
    const search = qs('[data-role="admin-product-search"]', root);
    const category = qs('[data-role="admin-product-category"]', root);
    category.insertAdjacentHTML('beforeend', categories.map((item) =>
        `<option value="${item.idCategory}">${escapeHtml(item.nameCategory)}</option>`).join(''));
    let page = 1;
    let response;
    let rows = [];

    const drawRows = () => {
        const term = search.value.trim().toLowerCase();
        const categoryId = category.value;
        const filtered = rows.filter((product) => (!term || `${product.nameProduct} ${product.categoryName}`.toLowerCase().includes(term))
            && (!categoryId || String(product.idCategory) === categoryId));
        qs('[data-role="admin-product-meta"]', root).textContent =
            `${filtered.length} bản ghi · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((product) => `<tr>
            <td><div class="table-product"><span class="table-thumb"><img src="${imageSrc(product.imageDTOS?.[0]?.imageBase64)}" alt=""></span>
                <div><strong class="table-primary">${escapeHtml(product.nameProduct)}</strong><span class="table-secondary">#${product.idProduct}</span></div></div></td>
            <td>${escapeHtml(product.categoryName || '—')}</td>
            <td><strong>${totalStock(product)} trong kho</strong><span class="table-secondary">${minPrice(product) ? `Từ ${formatCurrency(minPrice(product))}` : 'Chưa có giá'}</span></td>
            <td>${badge(product.status)}</td><td>${formatDate(product.updatedAt, true)}</td>
            <td class="align-right">${actionButtons(product.idProduct)}</td></tr>`).join('') ||
            `<tr><td colspan="6">${emptyState('Không có sản phẩm', 'Không tìm thấy dữ liệu phù hợp.', 'package')}</td></tr>`;
        bindRows();
        renderIcons(table);
    };
    const loadPage = async (nextPage = 1) => {
        page = nextPage;
        response = await api.adminProducts(page);
        rows = response.data || [];
        drawRows();
        bindPagination(qs('[data-role="admin-product-pagination"]', root), response, loadPage);
    };
    const bindRows = () => qsa('[data-row-action]', table).forEach((button) => button.addEventListener('click', async () => {
        const product = rows.find((item) => String(item.idProduct) === button.dataset.id);
        if (button.dataset.rowAction === 'view') await showProductDetail(button.dataset.id, () => loadPage(page));
        if (button.dataset.rowAction === 'edit') openProductForm(product, categories, () => loadPage(page));
        if (button.dataset.rowAction === 'delete') {
            const confirmed = await confirmAction({
                title: 'Xóa sản phẩm?',
                message: `"${product?.nameProduct || 'Sản phẩm'}" và dữ liệu liên quan sẽ bị xóa.`,
                confirmLabel: 'Xóa sản phẩm'
            });
            if (!confirmed) return;
            try {
                await api.deleteProduct(button.dataset.id);
                await loadProducts(true);
                toast('Đã xóa sản phẩm.');
                await loadPage(page);
            } catch (error) { toast(humanizeError(error), 'error'); }
        }
    }));
    search.addEventListener('input', debounce(drawRows, 160));
    category.addEventListener('change', drawRows);
    qs('[data-action="add-product"]', root).addEventListener('click', () => openProductForm(null, categories, () => loadPage(1)));
    await loadPage(1);
    if (sessionStorage.getItem('pawcare.quickAction') === 'product') {
        sessionStorage.removeItem('pawcare.quickAction');
        openProductForm(null, categories, () => loadPage(1));
    }
}

function categoryForm(category) {
    return `<form data-modal-form class="form-stack">${category ? `<input type="hidden" name="idCategory" value="${category.idCategory}">` : ''}
        <label class="form-field"><span>Tên danh mục</span><input name="nameCategory" required minlength="2"
            value="${escapeHtml(category?.nameCategory || '')}" placeholder="Ví dụ: Dinh dưỡng"></label>
        <label class="form-field"><span>Mô tả</span><textarea name="description" required
            placeholder="Mô tả ngắn về nhóm sản phẩm">${escapeHtml(category?.description || '')}</textarea></label>
        <div class="form-actions"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${category ? 'Lưu thay đổi' : 'Thêm danh mục'}</button></div></form>`;
}

export async function renderAdminCategories({ root }) {
    let categories = await loadCategories(true);
    const table = qs('[data-role="category-table"]', root);
    const search = qs('[data-role="category-search"]', root);
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const filtered = categories.filter((item) => !term || `${item.nameCategory} ${item.description}`.toLowerCase().includes(term));
        qs('[data-role="category-meta"]', root).textContent = `${filtered.length} danh mục`;
        table.innerHTML = filtered.map((item) => `<tr><td><strong class="table-primary">${escapeHtml(item.nameCategory)}</strong>
            <span class="table-secondary">#${item.idCategory}</span></td><td>${escapeHtml(item.description || '—')}</td>
            <td>${formatDate(item.createdAt)}</td><td>${formatDate(item.updatedAt, true)}</td>
            <td>${actionButtons(item.idCategory, ['edit', 'delete'])}</td></tr>`).join('') ||
            `<tr><td colspan="5">${emptyState('Chưa có danh mục', 'Hãy thêm danh mục đầu tiên.', 'tags')}</td></tr>`;
        bind();
        renderIcons(table);
    };
    const reload = async () => { categories = await loadCategories(true); draw(); };
    const openForm = (item = null) => {
        openModal({ title: item ? 'Chỉnh sửa danh mục' : 'Thêm danh mục', eyebrow: 'Danh mục', content: categoryForm(item) });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            if (data.idCategory) data.idCategory = Number(data.idCategory);
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                if (item) await api.updateCategory(data); else await api.addCategory(data);
                closeModal(); toast(item ? 'Đã cập nhật danh mục.' : 'Đã thêm danh mục.'); await reload();
            } catch (error) { toast(humanizeError(error), 'error'); setButtonLoading(button, false); }
        });
    };
    const bind = () => qsa('[data-row-action]', table).forEach((button) => button.addEventListener('click', async () => {
        const item = categories.find((entry) => String(entry.idCategory) === button.dataset.id);
        if (button.dataset.rowAction === 'edit') openForm(item);
        else {
            const confirmed = await confirmAction({
                title: 'Xóa danh mục?', message: `Bạn có chắc muốn xóa "${item?.nameCategory}"?`, confirmLabel: 'Xóa danh mục'
            });
            if (!confirmed) return;
            try { await api.deleteCategory(button.dataset.id); toast('Đã xóa danh mục.'); await reload(); }
            catch (error) { toast(humanizeError(error), 'error'); }
        }
    }));
    search.addEventListener('input', debounce(draw, 160));
    qs('[data-action="add-category"]', root).addEventListener('click', () => openForm());
    draw();
}

function sizeForm(item) {
    return `<form data-modal-form class="form-stack">${item ? `<input type="hidden" name="idSize" value="${item.idSize}">` : ''}
        <label class="form-field"><span>Tên kích cỡ</span><input name="size" required maxlength="30"
            value="${escapeHtml(item?.size || '')}" placeholder="Ví dụ: S, M, 1kg, 500ml"></label>
        <div class="form-actions"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${item ? 'Lưu thay đổi' : 'Thêm kích cỡ'}</button></div></form>`;
}

export async function renderAdminSizes({ root }) {
    let sizes = (await api.sizes()).data || [];
    const grid = qs('[data-role="size-grid"]', root);
    const draw = () => {
        grid.innerHTML = sizes.map((item) => `<article class="size-card"><div>
            <span class="eyebrow">#${item.idSize}</span><span class="size-token">${escapeHtml(item.size)}</span></div>
            <div class="size-card-actions"><button class="icon-button" data-edit-size="${item.idSize}" data-icon="edit" aria-label="Sửa"></button>
                <button class="icon-button" data-delete-size="${item.idSize}" data-icon="trash" aria-label="Xóa"></button></div></article>`).join('') ||
            emptyState('Chưa có kích cỡ', 'Thêm kích cỡ để tạo biến thể sản phẩm.', 'ruler');
        renderIcons(grid);
        qsa('[data-edit-size]', grid).forEach((button) => button.addEventListener('click', () =>
            openForm(sizes.find((item) => String(item.idSize) === button.dataset.editSize))));
        qsa('[data-delete-size]', grid).forEach((button) => button.addEventListener('click', async () => {
            const item = sizes.find((entry) => String(entry.idSize) === button.dataset.deleteSize);
            const confirmed = await confirmAction({
                title: 'Xóa kích cỡ?', message: `Xóa kích cỡ "${item?.size}" có thể ảnh hưởng biến thể đang dùng.`, confirmLabel: 'Xóa kích cỡ'
            });
            if (!confirmed) return;
            try { await api.deleteSize(button.dataset.deleteSize); toast('Đã xóa kích cỡ.'); await reload(); }
            catch (error) { toast(humanizeError(error), 'error'); }
        }));
    };
    const reload = async () => { sizes = (await api.sizes()).data || []; draw(); };
    const openForm = (item = null) => {
        openModal({ title: item ? 'Chỉnh sửa kích cỡ' : 'Thêm kích cỡ', eyebrow: 'Thuộc tính', content: sizeForm(item) });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            if (data.idSize) data.idSize = Number(data.idSize);
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                if (item) await api.updateSize(data); else await api.addSize(data);
                closeModal(); toast(item ? 'Đã cập nhật kích cỡ.' : 'Đã thêm kích cỡ.'); await reload();
            } catch (error) { toast(humanizeError(error), 'error'); setButtonLoading(button, false); }
        });
    };
    qs('[data-action="add-size"]', root).addEventListener('click', () => openForm());
    draw();
}

function voucherStatus(item) {
    if (new Date(item.expiredDate) < new Date(new Date().toDateString())) return 'EXPIRED';
    return Number(item.quantity) > 0 ? 'AVAILABLE' : 'INACTIVE';
}

function voucherForm(item) {
    return `<form data-modal-form class="form-grid">${item ? `<input type="hidden" name="idVoucher" value="${item.idVoucher}">` : ''}
        <label class="form-field"><span>Mã voucher</span><input name="code" required maxlength="30"
            value="${escapeHtml(item?.code || '')}" placeholder="PAWCARE20" style="text-transform:uppercase"></label>
        <label class="form-field"><span>Mức giảm (%)</span><input name="discount" type="number" min="1" max="100"
            value="${item?.discount ?? ''}" required></label>
        <label class="form-field"><span>Số lượng</span><input name="quantity" type="number" min="0"
            value="${item?.quantity ?? ''}" required></label>
        <label class="form-field"><span>Ngày hết hạn</span><input name="expiredDate" type="date" min="${new Date().toISOString().slice(0, 10)}"
            value="${toDateInput(item?.expiredDate)}" required></label>
        <div class="form-actions form-span-2"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${item ? 'Lưu thay đổi' : 'Tạo voucher'}</button></div></form>`;
}

export async function renderAdminVouchers({ root }) {
    const table = qs('[data-role="voucher-table"]', root);
    const search = qs('[data-role="voucher-search"]', root);
    let response;
    let rows = [];
    let page = 1;
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const filtered = rows.filter((item) => !term || item.code.toLowerCase().includes(term));
        qs('[data-role="voucher-meta"]', root).textContent = `${filtered.length} bản ghi · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((item) => `<tr><td><strong class="table-primary">${escapeHtml(item.code)}</strong>
            <span class="table-secondary">#${item.idVoucher}</span></td><td><strong>${item.discount}%</strong></td>
            <td>${item.quantity}</td><td>${formatDate(item.expiredDate)}</td><td>${badge(voucherStatus(item))}</td>
            <td>${actionButtons(item.idVoucher, ['edit', 'delete'])}</td></tr>`).join('') ||
            `<tr><td colspan="6">${emptyState('Chưa có voucher', 'Tạo mã ưu đãi đầu tiên cho khách hàng.', 'ticket')}</td></tr>`;
        bindRows(); renderIcons(table);
    };
    const loadPage = async (next = 1) => {
        page = next; response = await api.adminVouchers(page); rows = response.data || []; draw();
        bindPagination(qs('[data-role="voucher-pagination"]', root), response, loadPage);
    };
    const openForm = (item = null) => {
        openModal({ title: item ? 'Chỉnh sửa voucher' : 'Tạo voucher', eyebrow: 'Khuyến mãi', content: voucherForm(item) });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            const payload = {
                idVoucher: data.idVoucher ? Number(data.idVoucher) : null,
                code: data.code.trim().toUpperCase(), discount: Number(data.discount),
                expiredDate: data.expiredDate, quantity: Number(data.quantity)
            };
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                if (item) await api.updateVoucher(payload); else await api.addVoucher(payload);
                closeModal(); toast(item ? 'Đã cập nhật voucher.' : 'Đã tạo voucher.'); await loadPage(page);
            } catch (error) { toast(humanizeError(error), 'error'); setButtonLoading(button, false); }
        });
    };
    const bindRows = () => qsa('[data-row-action]', table).forEach((button) => button.addEventListener('click', async () => {
        const item = rows.find((entry) => String(entry.idVoucher) === button.dataset.id);
        if (button.dataset.rowAction === 'edit') openForm(item);
        else {
            const confirmed = await confirmAction({
                title: 'Xóa voucher?', message: `Mã "${item?.code}" sẽ không còn sử dụng được.`, confirmLabel: 'Xóa voucher'
            });
            if (!confirmed) return;
            try { await api.deleteVoucher(button.dataset.id); toast('Đã xóa voucher.'); await loadPage(page); }
            catch (error) { toast(humanizeError(error), 'error'); }
        }
    }));
    search.addEventListener('input', debounce(draw, 160));
    qs('[data-action="add-voucher"]', root).addEventListener('click', () => openForm());
    await loadPage();
    if (sessionStorage.getItem('pawcare.quickAction') === 'voucher') {
        sessionStorage.removeItem('pawcare.quickAction'); openForm();
    }
}

function importForm(item, products, sizes) {
    return `<form data-modal-form class="form-grid">
        <label class="form-field"><span>Sản phẩm</span><select name="idProduct" required ${item ? 'disabled' : ''}>
            <option value="">Chọn sản phẩm</option>${products.map((product) => `<option value="${product.idProduct}"
                ${Number(item?.idProduct) === Number(product.idProduct) ? 'selected' : ''}>${escapeHtml(product.nameProduct)}</option>`).join('')}</select>
            ${item ? `<input type="hidden" name="idProduct" value="${item.idProduct}">` : ''}</label>
        <label class="form-field"><span>Kích cỡ</span><select name="idSize" required ${item ? 'disabled' : ''}>
            <option value="">Chọn kích cỡ</option>${sizes.map((size) => `<option value="${size.idSize}"
                ${Number(item?.idSize) === Number(size.idSize) ? 'selected' : ''}>${escapeHtml(size.size)}</option>`).join('')}</select>
            ${item ? `<input type="hidden" name="idSize" value="${item.idSize}">` : ''}</label>
        <label class="form-field"><span>Giá nhập</span><input name="importPrice" type="number" min="0" step="1000"
            value="${item?.importPrice ?? ''}" required></label>
        <label class="form-field"><span>Số lượng</span><input name="quantity" type="number" min="1"
            value="${item?.quantity ?? ''}" required></label>
        <label class="form-field form-span-2"><span>Ngày nhập</span><input name="importDate" type="datetime-local"
            value="${toDateTimeInput(item?.importDate)}" required></label>
        <div class="form-actions form-span-2"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
            <button class="button button-primary" type="submit">${item ? 'Lưu phiếu nhập' : 'Tạo phiếu nhập'}</button></div></form>`;
}

export async function renderAdminImports({ root }) {
    const [products, sizeResponse] = await Promise.all([loadProducts(true), api.sizes()]);
    const sizes = sizeResponse.data || [];
    const table = qs('[data-role="import-table"]', root);
    const search = qs('[data-role="import-search"]', root);
    let response;
    let rows = [];
    let page = 1;
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const filtered = rows.filter((item) => !term || item.productName.toLowerCase().includes(term));
        qs('[data-role="import-meta"]', root).textContent = `${filtered.length} phiếu · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((item) => `<tr><td><strong class="table-primary">#${item.idImportProduct}</strong></td>
            <td><strong class="table-primary">${escapeHtml(item.productName)}</strong><span class="table-secondary">#${item.idProduct}</span></td>
            <td>${escapeHtml(item.size)}</td><td>${formatCurrency(item.importPrice)}</td><td><strong>${item.quantity}</strong></td>
            <td>${formatDate(item.importDate, true)}</td><td>${actionButtons(item.idImportProduct, ['edit', 'delete'])}</td></tr>`).join('') ||
            `<tr><td colspan="7">${emptyState('Chưa có phiếu nhập', 'Tạo phiếu nhập kho đầu tiên.', 'warehouse')}</td></tr>`;
        bindRows(); renderIcons(table);
    };
    const loadPage = async (next = 1) => {
        page = next; response = await api.imports(page); rows = response.data || []; draw();
        bindPagination(qs('[data-role="import-pagination"]', root), response, loadPage);
    };
    const openForm = (item = null) => {
        openModal({ title: item ? 'Chỉnh sửa phiếu nhập' : 'Tạo phiếu nhập', eyebrow: 'Kho hàng', content: importForm(item, products, sizes), size: 'wide' });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = qs('button[type="submit"]', form);
            const data = formDataObject(form);
            const date = data.importDate.length === 16 ? `${data.importDate}:00` : data.importDate;
            const payload = {
                idProduct: Number(data.idProduct), idSize: Number(data.idSize),
                importPrice: Number(data.importPrice), quantity: Number(data.quantity), importDate: date
            };
            setButtonLoading(button, true, 'Đang lưu...');
            try {
                if (item) await api.updateImport(payload); else await api.addImport(payload);
                closeModal(); toast(item ? 'Đã cập nhật phiếu nhập.' : 'Đã tạo phiếu nhập.'); await loadPage(page);
            } catch (error) { toast(humanizeError(error), 'error'); setButtonLoading(button, false); }
        });
    };
    const bindRows = () => qsa('[data-row-action]', table).forEach((button) => button.addEventListener('click', async () => {
        const item = rows.find((entry) => String(entry.idImportProduct) === button.dataset.id);
        if (button.dataset.rowAction === 'edit') openForm(item);
        else {
            const confirmed = await confirmAction({
                title: 'Xóa phiếu nhập?', message: `Phiếu nhập #${item?.idImportProduct} sẽ bị xóa.`, confirmLabel: 'Xóa phiếu'
            });
            if (!confirmed) return;
            try { await api.deleteImport(button.dataset.id); toast('Đã xóa phiếu nhập.'); await loadPage(page); }
            catch (error) { toast(humanizeError(error), 'error'); }
        }
    }));
    search.addEventListener('input', debounce(draw, 160));
    qs('[data-action="add-import"]', root).addEventListener('click', () => openForm());
    await loadPage();
    if (sessionStorage.getItem('pawcare.quickAction') === 'import') {
        sessionStorage.removeItem('pawcare.quickAction'); openForm();
    }
}

function orderDetail(order) {
    const address = order.informationOrderDTO || {};
    return `<div class="detail-list">
        <div class="detail-item"><span>Mã đơn</span><strong>#${order.idOrder}</strong></div>
        <div class="detail-item"><span>Trạng thái</span><strong>${badge(order.status)}</strong></div>
        <div class="detail-item"><span>Khách hàng</span><strong>${escapeHtml(order.nameUser || '—')}</strong></div>
        <div class="detail-item"><span>Người nhận</span><strong>${escapeHtml(address.nameOrder || '—')}</strong></div>
        <div class="detail-item"><span>Điện thoại</span><strong>${escapeHtml(address.phoneOrder || '—')}</strong></div>
        <div class="detail-item"><span>Địa chỉ</span><strong>${escapeHtml(address.addressOrder || '—')}</strong></div>
        <div class="detail-item"><span>Thanh toán</span><strong>${escapeHtml(order.paymentMethod || '—')}</strong></div>
        <div class="detail-item"><span>Voucher</span><strong>${escapeHtml(order.voucherCode || 'Không áp dụng')}</strong></div></div>
        <div class="modal-section"><h3>Sản phẩm</h3>${(order.orderDetailDTOS || []).map((item) => `<div class="order-detail-item">
            <img src="${imageSrc(item.imageProduct)}" alt=""><div><strong>${escapeHtml(item.nameProduct)}</strong>
            <span>${escapeHtml(item.size)} · SL ${item.quantity} · ${formatCurrency(item.price)}/sp</span></div>
            <strong>${formatCurrency(item.totalPrice)}</strong></div>`).join('')}</div>
        <div class="summary-total"><span>Tổng thanh toán</span><strong>${formatCurrency(order.totalAmount)}</strong></div>
        ${order.note ? `<div class="modal-section"><h3>Ghi chú</h3><p>${escapeHtml(order.note)}</p></div>` : ''}`;
}

export async function renderAdminOrders({ root }) {
    const table = qs('[data-role="admin-order-table"]', root);
    const search = qs('[data-role="admin-order-search"]', root);
    const statusFilter = qs('[data-role="admin-order-status"]', root);
    let response;
    let rows = [];
    let page = 1;
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const status = statusFilter.value;
        const filtered = rows.filter((item) => (!term || `#${item.idOrder} ${item.nameUser}`.toLowerCase().includes(term))
            && (!status || item.status === status));
        qs('[data-role="admin-order-meta"]', root).textContent = `${filtered.length} đơn · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((order) => `<tr><td><strong class="table-primary">#${order.idOrder}</strong>
            <span class="table-secondary">${order.orderDetailDTOS?.length || 0} sản phẩm</span></td>
            <td><strong class="table-primary">${escapeHtml(order.nameUser || '—')}</strong><span class="table-secondary">User #${order.idUser}</span></td>
            <td>${formatDate(order.createdAt, true)}</td><td><strong>${formatCurrency(order.totalAmount)}</strong></td>
            <td>${escapeHtml(order.paymentMethod || '—')}</td><td>${badge(order.status)}</td>
            <td>${actionButtons(order.idOrder, ['view', 'edit', 'delete'])}</td></tr>`).join('') ||
            `<tr><td colspan="7">${emptyState('Không có đơn hàng', 'Không tìm thấy đơn phù hợp.', 'clipboard-list')}</td></tr>`;
        bindRows(); renderIcons(table);
    };
    const loadPage = async (next = 1) => {
        page = next; response = await api.orders(page); rows = response.data || []; draw();
        bindPagination(qs('[data-role="admin-order-pagination"]', root), response, loadPage);
    };
    const openStatus = (order) => {
        const statuses = ['WAITING CONFIRMATION', 'CONFIRMED', 'PROCESSING', 'SHIPPING', 'COMPLETED', 'CANCELLED'];
        openModal({ title: `Cập nhật đơn #${order.idOrder}`, eyebrow: 'Trạng thái đơn hàng', content:
            `<form data-modal-form class="form-stack"><label class="form-field"><span>Trạng thái mới</span>
                <select name="status" required>${statuses.map((status) => `<option value="${status}" ${order.status === status ? 'selected' : ''}>
                    ${badge(status).replace(/<[^>]+>/g, '')}</option>`).join('')}</select></label>
                <div class="form-actions"><button class="button button-ghost" type="button" data-modal-cancel>Hủy</button>
                    <button class="button button-primary" type="submit">Cập nhật</button></div></form>` });
        qs('[data-modal-cancel]').addEventListener('click', closeModal);
        const form = qs('[data-modal-form]');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const button = qs('button[type="submit"]', form);
            setButtonLoading(button, true, 'Đang cập nhật...');
            try {
                await api.updateOrderStatus(order.idOrder, form.status.value);
                closeModal(); toast('Đã cập nhật trạng thái đơn hàng.'); await loadPage(page);
            } catch (error) { toast(humanizeError(error), 'error'); setButtonLoading(button, false); }
        });
    };
    const bindRows = () => qsa('[data-row-action]', table).forEach((button) => button.addEventListener('click', async () => {
        const order = rows.find((entry) => String(entry.idOrder) === button.dataset.id);
        if (button.dataset.rowAction === 'view') {
            try {
                const detail = (await api.order(button.dataset.id)).data;
                openModal({ title: `Chi tiết đơn #${button.dataset.id}`, eyebrow: 'Đơn hàng', content: orderDetail(detail), size: 'wide' });
            } catch (error) { toast(humanizeError(error), 'error'); }
        }
        if (button.dataset.rowAction === 'edit') openStatus(order);
        if (button.dataset.rowAction === 'delete') {
            const confirmed = await confirmAction({
                title: 'Xóa đơn hàng?', message: `Đơn #${order.idOrder} sẽ bị xóa vĩnh viễn.`, confirmLabel: 'Xóa đơn hàng'
            });
            if (!confirmed) return;
            try { await api.deleteOrder(order.idOrder); toast('Đã xóa đơn hàng.'); await loadPage(page); }
            catch (error) { toast(humanizeError(error), 'error'); }
        }
    }));
    search.addEventListener('input', debounce(draw, 160));
    statusFilter.addEventListener('change', draw);
    await loadPage();
}

export async function renderAdminUsers({ root }) {
    const table = qs('[data-role="user-table"]', root);
    const search = qs('[data-role="user-search"]', root);
    let response;
    let rows = [];
    let page = 1;
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const filtered = rows.filter((item) => !term || `${item.name} ${item.email}`.toLowerCase().includes(term));
        qs('[data-role="user-meta"]', root).textContent = `${filtered.length} tài khoản · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((user) => `<tr><td><div class="table-product">
            <span class="avatar${String(user.role).toUpperCase() === 'ADMIN' ? ' admin' : ''}">${initials(user.name)}</span>
            <div><strong class="table-primary">${escapeHtml(user.name)}</strong><span class="table-secondary">${escapeHtml(user.email)}</span></div></div></td>
            <td>${badge(user.role)}</td><td>${user.informationOrderDTOS?.length || 0} địa chỉ</td>
            <td>${formatDate(user.createdAt)}</td><td>${formatDate(user.updatedAt, true)}</td>
            <td>${actionButtons(user.idUser, ['view'])}</td></tr>`).join('') ||
            `<tr><td colspan="6">${emptyState('Chưa có tài khoản', 'Người dùng đăng ký sẽ xuất hiện tại đây.', 'users')}</td></tr>`;
        qsa('[data-row-action="view"]', table).forEach((button) => button.addEventListener('click', () => {
            const user = rows.find((entry) => String(entry.idUser) === button.dataset.id);
            openModal({ title: user.name, eyebrow: 'Thông tin người dùng', content: `<div class="detail-list">
                <div class="detail-item"><span>ID</span><strong>#${user.idUser}</strong></div>
                <div class="detail-item"><span>Vai trò</span><strong>${badge(user.role)}</strong></div>
                <div class="detail-item"><span>Email</span><strong>${escapeHtml(user.email)}</strong></div>
                <div class="detail-item"><span>Ngày tham gia</span><strong>${formatDate(user.createdAt, true)}</strong></div></div>
                <div class="modal-section"><h3>Sổ địa chỉ</h3>${(user.informationOrderDTOS || []).map((address) =>
                    `<div class="address-card" style="margin-bottom:8px"><h3>${escapeHtml(address.nameOrder)}</h3>
                    <p>${escapeHtml(address.addressOrder)}</p><small>${escapeHtml(address.phoneOrder)}</small></div>`).join('') ||
                    '<p class="table-secondary">Chưa có địa chỉ.</p>'}</div>`, size: 'wide' });
        }));
        renderIcons(table);
    };
    const loadPage = async (next = 1) => {
        page = next; response = await api.users(page); rows = response.data || []; draw();
        bindPagination(qs('[data-role="user-pagination"]', root), response, loadPage);
    };
    search.addEventListener('input', debounce(draw, 160));
    await loadPage();
}

function reviewDetail(review) {
    return `<div class="detail-list">
        <div class="detail-item"><span>Mã đánh giá</span><strong>#${review.idReview}</strong></div>
        <div class="detail-item"><span>Số sao</span><strong class="review-stars">${ratingStars(review.star)}</strong></div>
        <div class="detail-item"><span>Khách hàng</span><strong>${escapeHtml(review.nameUser || '—')}</strong></div>
        <div class="detail-item"><span>Ngày gửi</span><strong>${formatDate(review.createdAt, true)}</strong></div>
        </div>
        <div class="modal-section"><h3>Sản phẩm được đánh giá</h3>
            <div class="detail-list">
                <div class="detail-item"><span>Tên sản phẩm</span><strong>${escapeHtml(review.nameProduct || 'Sản phẩm')}</strong></div>
                <div class="detail-item"><span>Mã sản phẩm</span><strong>#${review.idProduct || '—'}</strong></div>
            </div></div>
        <div class="modal-section"><h3>Nội dung phản hồi</h3>
            <p>${escapeHtml(review.comment || 'Khách hàng chưa để lại nhận xét.')}</p></div>
        <p class="table-secondary">Admin chỉ xem đánh giá để theo dõi chất lượng sản phẩm/dịch vụ.</p>`;
}

export async function renderAdminReviews({ root }) {
    const table = qs('[data-role="review-table"]', root);
    const search = qs('[data-role="review-search"]', root);
    let response;
    let rows = [];
    let page = 1;
    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const filtered = rows.filter((item) => !term || [
            item.nameProduct, item.nameUser, item.comment, `#${item.idReview}`
        ].join(' ').toLowerCase().includes(term));
        qs('[data-role="review-meta"]', root).textContent = `${filtered.length} đánh giá · Trang ${response.currentPage}/${Math.max(response.totalPages, 1)}`;
        table.innerHTML = filtered.map((review) => `<tr><td><strong class="table-primary">#${review.idReview}</strong>
            <span class="table-secondary">${escapeHtml(review.comment || 'Chưa có nhận xét')}</span></td>
            <td><strong class="table-primary">${escapeHtml(review.nameProduct || 'Sản phẩm')}</strong>
                <span class="table-secondary">#${review.idProduct || '—'}</span></td>
            <td><strong class="table-primary">${escapeHtml(review.nameUser || 'Khách hàng')}</strong>
                <span class="table-secondary">User #${review.idUser || '—'}</span></td>
            <td><span class="review-stars">${ratingStars(review.star)}</span></td>
            <td>${formatDate(review.createdAt, true)}</td>
            <td>${actionButtons(review.idReview, ['view'])}</td></tr>`).join('') ||
            `<tr><td colspan="6">${emptyState('Chưa có đánh giá', 'Các đánh giá của khách hàng sẽ xuất hiện tại đây.', 'star')}</td></tr>`;
        qsa('[data-row-action="view"]', table).forEach((button) => button.addEventListener('click', () => {
            const review = rows.find((entry) => String(entry.idReview) === button.dataset.id);
            if (review) openModal({ title: `Đánh giá #${review.idReview}`, eyebrow: 'Phản hồi khách hàng', content: reviewDetail(review), size: 'wide' });
        }));
        renderIcons(table);
    };
    const loadPage = async (next = 1) => {
        page = next; response = await api.adminReviews(page); rows = response.data || []; draw();
        bindPagination(qs('[data-role="review-pagination"]', root), response, loadPage);
    };
    search.addEventListener('input', debounce(draw, 160));
    await loadPage();
}
