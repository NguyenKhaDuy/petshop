# PawCare Thymeleaf UI

## Cấu trúc

```text
src/main/resources/
├── templates/
│   ├── index.html
│   ├── layout/
│   │   ├── base.html
│   │   ├── header.html
│   │   ├── sidebar.html
│   │   ├── footer.html
│   │   └── alerts.html
│   ├── pages/
│   │   ├── store/home.html
│   │   ├── auth/auth.html
│   │   ├── customer/account.html
│   │   └── admin/
│   │       ├── dashboard.html
│   │       ├── products.html
│   │       ├── catalog.html
│   │       └── operations.html
│   └── error/
│       ├── error.html
│       ├── 404.html
│       └── 500.html
└── static/
    ├── css/
    │   ├── style.css
    │   ├── components.css
    │   └── responsive.css
    ├── js/
    │   ├── app.js
    │   ├── api.js
    │   ├── state.js
    │   ├── utils.js
    │   ├── error.js
    │   └── modules/
    │       ├── store.js
    │       ├── auth.js
    │       ├── customer.js
    │       └── admin.js
    └── images/
        ├── pawcare-mark.svg
        └── pet-placeholder.svg
```

## Màn hình và API sử dụng

| Màn hình | Hash route | API hiện có |
|---|---|---|
| Trang chủ | `#/home` | `GET /api/product`, `GET /api/category` |
| Danh sách sản phẩm | `#/products` | `GET /api/product`, `GET /api/category` |
| Chi tiết sản phẩm | `#/product/{id}` | `GET /api/product/id-product={id}`, `GET /api/admin/size` |
| Đăng nhập | `#/login` | `POST /api/login`, `GET /api/user/id-user={id}` |
| Đăng ký | `#/register` | `POST /api/register` |
| Wishlist | `#/wishlist` | GET/POST/DELETE `/api/wishlist...` |
| Giỏ hàng | `#/cart` | GET/POST/DELETE `/api/cart...`; người dùng chọn các cart item cần đặt |
| Thanh toán | Modal từ giỏ hàng | `GET /api/payment-method`, `GET /api/voucher` (chỉ voucher còn hiệu lực), `POST /api/order` |
| Đơn của khách | `#/orders` | `GET /api/order/user/id-user={id}`, `GET /api/order/id-order={id}`, `PUT /api/order/status` |
| Hồ sơ | `#/profile` | `GET /api/user/id-user={id}`, POST/DELETE `/api/information-order...`, `POST /api/change-password` |
| Dashboard admin | `#/admin` | Product/category/voucher/order/user APIs hiện có |
| Quản lý sản phẩm | `#/admin/products` | CRUD `/api/admin/product`, CRUD `/api/admin/product/size` |
| Danh mục | `#/admin/categories` | CRUD `/api/admin/category` |
| Kích cỡ | `#/admin/sizes` | CRUD `/api/admin/size` |
| Voucher | `#/admin/vouchers` | CRUD `/api/admin/voucher` |
| Nhập kho | `#/admin/imports` | CRUD `/api/admin/import/product` |
| Đơn hàng admin | `#/admin/orders` | GET/PUT/DELETE order APIs hiện có |
| Người dùng admin | `#/admin/users` | `GET /api/admin/user`, `GET /api/user/id-user={id}` |

## Chạy project

1. Khởi động MySQL và bảo đảm database `petshop` cùng schema hiện có đã sẵn sàng.
2. Kiểm tra cấu hình trong `src/main/resources/application.properties`.
3. Dùng Java 21.
4. Chạy:

```powershell
.\mvnw.cmd spring-boot:run
```

5. Mở `http://localhost:8080/`.

## Kiểm tra nhanh

- Không đăng nhập: trang chủ, catalog, chi tiết sản phẩm, đăng ký/đăng nhập.
- Tài khoản `USER`: wishlist, giỏ hàng, đơn hàng, địa chỉ và đổi mật khẩu.
- Tài khoản `ADMIN`: đăng nhập rồi mở `#/admin`.
- Thu nhỏ trình duyệt để kiểm tra sidebar mobile, card, bảng cuộn ngang và modal dạng bottom sheet.

Chi tiết các luồng chưa thể hoàn thiện do thiếu contract backend nằm trong
[`MISSING_ENDPOINTS.md`](MISSING_ENDPOINTS.md).
