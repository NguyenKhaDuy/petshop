# Các endpoint còn thiếu hoặc chưa đủ để hoàn thiện luồng UI

Tài liệu này chỉ ghi nhận khoảng trống của backend hiện tại. Giao diện **không tạo API giả** và
không bổ sung endpoint REST mới.

## 1. Cập nhật số lượng item trong giỏ

- Có:
  - `POST /api/cart/item`
  - `DELETE /api/cart/id-cart-item={id}`
- Thiếu endpoint cập nhật quantity của một cart item.
- UI chỉ hiển thị số lượng hiện tại và cho phép xóa; không hiển thị nút tăng/giảm giả.

## 2. Đánh giá sản phẩm

- DTO sản phẩm có danh sách review để đọc.
- Không có endpoint tạo, sửa hoặc xóa review.
- UI hiển thị review ở chế độ chỉ đọc.

## 3. Cập nhật hồ sơ và quản trị người dùng

- Có đọc user, thêm/xóa địa chỉ và đổi mật khẩu.
- Thiếu endpoint cập nhật tên/email, khóa/mở tài khoản, đổi role và xóa user.
- Trang hồ sơ chỉ sửa các phần backend đang hỗ trợ.
- Trang quản trị khách hàng chỉ đọc chi tiết.

## 4. Trạng thái sản phẩm

- `ProductDTO` có field `status`.
- `ProductRequest` và các endpoint thêm/sửa sản phẩm không nhận status.
- UI hiển thị trạng thái nhưng không cung cấp thao tác bật/tắt sản phẩm.

## 5. Session và phân quyền backend

- `POST /api/login` chỉ trả `idUser`, email và trạng thái; không tạo session/token.
- `SecurityConfig` đang `permitAll()` cho mọi request, kể cả `/api/admin/**`.
- Không có endpoint đăng xuất/current-user/refresh-session.
- UI lưu ID đăng nhập trong `localStorage` để duy trì trải nghiệm và đọc role qua
  `GET /api/user/id-user={id}`. Đây **không phải cơ chế bảo mật**.
- Khu vực admin được ẩn/chặn ở giao diện theo role, nhưng backend vẫn cần cơ chế xác thực và phân
  quyền thực sự trước khi triển khai production.

## 6. Dashboard thống kê

- Thiếu endpoint aggregate cho doanh thu, tổng đơn, tổng khách, tồn kho thấp và xu hướng theo thời gian.
- Các card dashboard chỉ tổng hợp từ dữ liệu endpoint hiện có. Với API phân trang, UI ghi rõ đây là
  số bản ghi của trang đang tải, không giả lập tổng số toàn hệ thống.

## 7. Tìm kiếm/lọc phía server

- Các endpoint danh sách chưa nhận tham số search, status, category hoặc sort.
- UI tìm kiếm/lọc client-side trên tập dữ liệu đang tải.
- Riêng catalog công khai dùng `/api/product` nên có thể lọc toàn bộ dữ liệu trả về; các bảng admin
  phân trang chỉ lọc trong trang hiện tại.

## 8. Route giao diện

- Backend ban đầu chỉ có `@RestController`, không có route trả Thymeleaf view.
- Project thêm duy nhất MVC view mapping `/` trong `WebViewConfig`; đây không phải REST API và không
  thay đổi endpoint hiện có.
- Các màn hình dùng hash route (`#/products`, `#/admin/orders`, ...) nên không cần thêm route backend.
