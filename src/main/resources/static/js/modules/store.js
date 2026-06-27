import { api } from '../api.js';
import {
    qs, qsa, renderIcons, formatCurrency, imageSrc, escapeHtml, badge, emptyState,
    debounce, toast, humanizeError, setButtonLoading, confirmAction, ratingStars
} from '../utils.js';
import {
    state, loadProducts, loadCategories, loadCustomerCollections, loadCurrentUser
} from '../state.js';

function productPrice(product) {
    const prices = (product.sizeProductDTOS || []).map((item) => Number(item.price)).filter(Number.isFinite);
    return prices.length ? Math.min(...prices) : 0;
}

function productStock(product) {
    return (product.sizeProductDTOS || []).reduce((sum, item) => sum + Number(item.quantity || 0), 0);
}

function isWished(productId) {
    return state.wishlist.some((item) => Number(item.idProduct) === Number(productId));
}

function productCard(product) {
    const stock = productStock(product);
    const wished = isWished(product.idProduct);
    return `<article class="product-card" data-product-id="${product.idProduct}">
        <a class="product-card-media" href="#/product/${product.idProduct}">
            <img src="${imageSrc(product.imageDTOS?.[0]?.imageBase64)}" alt="${escapeHtml(product.nameProduct)}" loading="lazy">
            <span class="product-card-badge">${badge(stock > 0 ? product.status || 'ACTIVE' : 'INACTIVE')}</span>
        </a>
        <button class="icon-button wishlist-button${wished ? ' is-active' : ''}" type="button"
            data-action="toggle-wishlist" data-id="${product.idProduct}" aria-label="${wished ? 'Bỏ yêu thích' : 'Thêm yêu thích'}"
            data-icon="heart"></button>
        <div class="product-card-body">
            <span class="product-category">${escapeHtml(product.categoryName || 'Chưa phân loại')}</span>
            <h3><a href="#/product/${product.idProduct}">${escapeHtml(product.nameProduct)}</a></h3>
            <div class="product-card-footer">
                <div class="price-block"><small>${stock > 0 ? `${stock} sản phẩm trong kho` : 'Tạm hết hàng'}</small>
                    <strong>${productPrice(product) ? `Từ ${formatCurrency(productPrice(product))}` : 'Liên hệ'}</strong></div>
                <a class="mini-action" href="#/product/${product.idProduct}" aria-label="Xem sản phẩm" data-icon="arrow-right"></a>
            </div>
        </div>
    </article>`;
}

function bindWishlist(root, refreshShell, rerender) {
    qsa('[data-action="toggle-wishlist"]', root).forEach((button) => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();
            event.stopPropagation();
            if (!state.session) {
                sessionStorage.setItem('pawcare.redirect', location.hash);
                location.hash = '#/login';
                return;
            }
            setButtonLoading(button, true, '');
            try {
                const productId = Number(button.dataset.id);
                const existing = state.wishlist.find((item) => Number(item.idProduct) === productId);
                if (existing) {
                    await api.deleteWishlist(existing.idWishlist);
                    toast('Đã bỏ sản phẩm khỏi danh sách yêu thích.');
                } else {
                    await api.addWishlist({ idUser: state.session.idUser, idProduct: productId });
                    toast('Đã lưu sản phẩm yêu thích.');
                }
                await loadCustomerCollections(true);
                await refreshShell();
                rerender?.();
            } catch (error) {
                toast(humanizeError(error), 'error');
                setButtonLoading(button, false);
            }
        });
    });
}

export async function renderHome({ root, refreshShell }) {
    const [products, categories] = await Promise.all([loadProducts(), loadCategories()]);
    qs('[data-role="register-cta"]', root)?.classList.toggle('is-hidden', Boolean(state.session));
    const categoryGrid = qs('[data-role="home-categories"]', root);
    categoryGrid.innerHTML = categories.slice(0, 4).map((category, index) => {
        const icons = ['package', 'heart', 'sparkles', 'store'];
        return `<a class="category-card" href="#/products" data-category="${category.idCategory}">
            <span class="category-icon" data-icon="${icons[index % icons.length]}"></span>
            <div><strong>${escapeHtml(category.nameCategory)}</strong>
                <span>${escapeHtml(category.description || 'Khám phá sản phẩm phù hợp')}</span></div></a>`;
    }).join('') || emptyState('Chưa có danh mục', 'Danh mục sẽ xuất hiện tại đây.', 'layers');
    qsa('[data-category]', categoryGrid).forEach((card) => card.addEventListener('click', () => {
        sessionStorage.setItem('pawcare.category', card.dataset.category);
    }));
    const featured = [...products].sort((a, b) => productStock(b) - productStock(a)).slice(0, 8);
    const featuredGrid = qs('[data-role="featured-products"]', root);
    featuredGrid.innerHTML = featured.map(productCard).join('') ||
        emptyState('Chưa có sản phẩm', 'Sản phẩm mới sẽ sớm được cập nhật.', 'package');
    bindWishlist(root, refreshShell, () => renderHome({ root, refreshShell }));
    renderIcons(root);
}

export async function renderProducts({ root, refreshShell }) {
    const [products, categories] = await Promise.all([loadProducts(), loadCategories()]);
    const grid = qs('[data-role="product-grid"]', root);
    const search = qs('[data-role="product-search"]', root);
    const categorySelect = qs('[data-role="category-filter"]', root);
    const sortSelect = qs('[data-role="product-sort"]', root);
    categorySelect.insertAdjacentHTML('beforeend', categories.map((category) =>
        `<option value="${category.idCategory}">${escapeHtml(category.nameCategory)}</option>`).join(''));
    const pendingSearch = sessionStorage.getItem('pawcare.search');
    const pendingCategory = sessionStorage.getItem('pawcare.category');
    if (pendingSearch) { search.value = pendingSearch; sessionStorage.removeItem('pawcare.search'); }
    if (pendingCategory) { categorySelect.value = pendingCategory; sessionStorage.removeItem('pawcare.category'); }

    const draw = () => {
        const term = search.value.trim().toLowerCase();
        const category = categorySelect.value;
        const sort = sortSelect.value;
        let filtered = products.filter((product) => {
            const matchesTerm = !term || `${product.nameProduct} ${product.categoryName} ${product.description}`.toLowerCase().includes(term);
            return matchesTerm && (!category || String(product.idCategory) === category);
        });
        filtered = [...filtered].sort((a, b) => {
            if (sort === 'name-asc') return a.nameProduct.localeCompare(b.nameProduct, 'vi');
            if (sort === 'price-asc') return productPrice(a) - productPrice(b);
            if (sort === 'price-desc') return productPrice(b) - productPrice(a);
            return new Date(b.updatedAt || b.createdAt) - new Date(a.updatedAt || a.createdAt);
        });
        qs('[data-role="product-count"]', root).textContent = filtered.length;
        grid.innerHTML = filtered.map(productCard).join('') ||
            emptyState('Không tìm thấy sản phẩm', 'Hãy thử từ khóa hoặc danh mục khác.', 'search',
                '<button class="button button-ghost" data-action="clear-filter">Xóa bộ lọc</button>');
        qs('[data-action="clear-filter"]', grid)?.addEventListener('click', () => {
            search.value = ''; categorySelect.value = ''; draw();
        });
        bindWishlist(grid, refreshShell, draw);
        renderIcons(grid);
    };
    search.addEventListener('input', debounce(draw, 180));
    categorySelect.addEventListener('change', draw);
    sortSelect.addEventListener('change', draw);
    draw();
}

export async function renderProductDetail({ root, params, refreshShell }) {
    const [response, sizeResponse] = await Promise.all([api.product(params.id), api.sizes()]);
    const product = response.data;
    const sizes = sizeResponse.data || [];
    const container = qs('[data-role="product-detail"]', root);
    qs('[data-role="breadcrumb-name"]', root).textContent = product.nameProduct;
    const images = product.imageDTOS?.length ? product.imageDTOS : [{ imageBase64: null }];
    const variants = (product.sizeProductDTOS || []).map((variant) => ({
        ...variant,
        idSize: sizes.find((size) => String(size.size).toLowerCase() === String(variant.size).toLowerCase())?.idSize
    }));
    const firstAvailable = variants.find((item) => Number(item.quantity) > 0) || variants[0];
    const wished = isWished(product.idProduct);
    container.innerHTML = `<div class="product-gallery">
        <div class="product-main-image"><img data-role="main-image" src="${imageSrc(images[0].imageBase64)}"
            alt="${escapeHtml(product.nameProduct)}"></div>
        <div class="thumbnail-row">${images.map((image, index) => `<button class="thumbnail${index === 0 ? ' is-active' : ''}"
            data-image="${imageSrc(image.imageBase64)}" type="button"><img src="${imageSrc(image.imageBase64)}" alt=""></button>`).join('')}</div>
        </div>
        <div class="product-info">
            <span class="product-category">${escapeHtml(product.categoryName || 'Sản phẩm')}</span>
            <h1>${escapeHtml(product.nameProduct)}</h1>
            <div>${badge(product.status || 'ACTIVE')}</div>
            <div class="detail-price" data-role="detail-price">${firstAvailable ? formatCurrency(firstAvailable.price) : 'Liên hệ'}</div>
            <p class="description">${escapeHtml(product.description || 'Chưa có mô tả cho sản phẩm này.')}</p>
            <div class="option-label">Kích cỡ <span data-role="stock-label"></span></div>
            <div class="size-options">${variants.map((variant) => `<button class="size-option${variant === firstAvailable ? ' is-active' : ''}"
                type="button" data-size-id="${variant.idSize || ''}" data-price="${variant.price}" data-stock="${variant.quantity}"
                ${Number(variant.quantity) <= 0 || !variant.idSize ? 'disabled' : ''}>${escapeHtml(variant.size)}</button>`).join('') ||
                '<span class="table-secondary">Chưa có biến thể kích cỡ.</span>'}</div>
            <div class="option-label">Số lượng</div>
            <div class="quantity-control"><button type="button" data-quantity="minus" aria-label="Giảm" data-icon="minus"></button>
                <input type="number" min="1" value="1" data-role="quantity" aria-label="Số lượng">
                <button type="button" data-quantity="plus" aria-label="Tăng" data-icon="plus"></button></div>
            <div class="detail-actions">
                <button class="button button-primary" data-action="add-cart" ${firstAvailable?.idSize ? '' : 'disabled'}>
                    <span data-icon="shopping-bag"></span> Thêm vào giỏ</button>
                <button class="button button-ghost${wished ? ' is-active' : ''}" data-action="toggle-wishlist"
                    data-id="${product.idProduct}"><span data-icon="heart"></span> ${wished ? 'Đã yêu thích' : 'Yêu thích'}</button>
            </div>
        </div>`;

    let selected = firstAvailable;
    const updateVariant = (button) => {
        qsa('.size-option', container).forEach((item) => item.classList.toggle('is-active', item === button));
        selected = variants.find((variant) => String(variant.idSize) === button.dataset.sizeId);
        qs('[data-role="detail-price"]', container).textContent = formatCurrency(button.dataset.price);
        qs('[data-role="stock-label"]', container).textContent = `Còn ${button.dataset.stock} sản phẩm`;
        const quantity = qs('[data-role="quantity"]', container);
        quantity.max = button.dataset.stock;
        if (Number(quantity.value) > Number(button.dataset.stock)) quantity.value = button.dataset.stock;
    };
    const activeSize = qs('.size-option.is-active', container);
    if (activeSize && !activeSize.disabled) updateVariant(activeSize);
    qsa('.size-option', container).forEach((button) => button.addEventListener('click', () => updateVariant(button)));
    qsa('.thumbnail', container).forEach((button) => button.addEventListener('click', () => {
        qs('[data-role="main-image"]', container).src = button.dataset.image;
        qsa('.thumbnail', container).forEach((item) => item.classList.toggle('is-active', item === button));
    }));
    qsa('[data-quantity]', container).forEach((button) => button.addEventListener('click', () => {
        const input = qs('[data-role="quantity"]', container);
        const max = Number(input.max || selected?.quantity || 1);
        const delta = button.dataset.quantity === 'plus' ? 1 : -1;
        input.value = Math.min(max, Math.max(1, Number(input.value || 1) + delta));
    }));
    qs('[data-action="add-cart"]', container)?.addEventListener('click', async (event) => {
        if (!state.session) {
            sessionStorage.setItem('pawcare.redirect', location.hash);
            location.hash = '#/login';
            return;
        }
        const button = event.currentTarget;
        setButtonLoading(button, true, 'Đang thêm...');
        try {
            await api.addToCart({
                idUser: state.session.idUser,
                productId: product.idProduct,
                quantity: Number(qs('[data-role="quantity"]', container).value),
                idSize: selected.idSize
            });
            await loadCustomerCollections(true);
            await refreshShell();
            toast('Đã thêm sản phẩm vào giỏ hàng.');
        } catch (error) {
            toast(humanizeError(error), 'error');
        } finally {
            setButtonLoading(button, false);
        }
    });
    bindWishlist(container, refreshShell, () => renderProductDetail({ root, params, refreshShell }));
    const reviews = qs('[data-role="review-list"]', root);
    const canDeleteOwnReview = (review) => state.session
        && String(state.user?.role || state.session?.role || '').toUpperCase() !== 'ADMIN'
        && Number(review.idUser) === Number(state.session.idUser);
    reviews.innerHTML = (product.reviewProductDTOS || []).map((review) => `<article class="review-card">
        <div class="review-stars">${ratingStars(review.star)}</div>
        <p>${escapeHtml(review.comment || 'Người dùng chưa để lại nhận xét.')}</p>
        <div class="review-author"><span class="avatar">${escapeHtml((review.nameUser || 'K')[0])}</span>
            <div><strong>${escapeHtml(review.nameUser || 'Khách hàng')}</strong><span>${review.createdAt ? new Date(review.createdAt).toLocaleDateString('vi-VN') : ''}</span></div></div>
        ${canDeleteOwnReview(review)
            ? `<div class="review-card-actions"><button class="button button-danger button-small" type="button"
                data-delete-review="${review.idReview}">Xóa đánh giá của tôi</button></div>` : ''}
        </article>`).join('') ||
        emptyState('Chưa có đánh giá', 'Sản phẩm này chưa nhận được đánh giá từ khách hàng.', 'star');
    qsa('[data-delete-review]', reviews).forEach((button) => button.addEventListener('click', async () => {
        const confirmed = await confirmAction({
            title: 'Xóa đánh giá?',
            message: 'Đánh giá này sẽ bị xóa khỏi sản phẩm. Bạn có thể đánh giá lại trong đơn hàng đã hoàn tất.',
            confirmLabel: 'Xóa đánh giá'
        });
        if (!confirmed) return;
        try {
            await api.deleteReview(button.dataset.deleteReview, state.session.idUser);
            toast('Đã xóa đánh giá của bạn.');
            await renderProductDetail({ root, params, refreshShell });
        } catch (error) {
            toast(humanizeError(error), 'error');
        }
    }));
    renderIcons(root);
}
