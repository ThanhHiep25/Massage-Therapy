# HỆ THỐNG QUẢN LÝ SPA MASSAGE

## Giới thiệu
Hệ thống quản lý SPA Massage là một nền tảng giúp quản lý tổng thể dịch vụ, khách hàng, và nhân viên trong SPA. Hệ thống cung cấp các tính năng chính như:

- Đăng ký/Quản lý tài khoản khách hàng.
- Quản lý dịch vụ massage.
- Đặt lịch hẹn trực tuyến.
- Phân quyền người dùng (Admin, Nhân viên, Khách hàng).
- Tích hợp API thanh toán.

## Các tính năng chính
1. **Quản lý tài khoản:**
   - Tăng cường bảo mật bằng bcrypt hashing cho mật khẩu.
   - Hỗ trợ xác thực JWT cho API.

2. **Quản lý dịch vụ:**
   - Thêm, cập nhật, xóa dịch vụ.
   - Lưu trữ thông tin như: Tên dịch vụ, mô tả, thời lượng, giá cả.

3. **Đặt lịch:**
   - Khách hàng có thể xem lịch hẹn còn trống và đặt dịch vụ trực tuyến.

4. **Tích hợp thanh toán:**
   - Hỗ trợ các hình thức thanh toán như: Google Pay, MoMo, Zalo Pay.

5. **Quản lý phân quyền:**
   - Admin: Quản lý dữ liệu toàn hệ thông.
   - Nhân viên: Quản lý lịch hẹn và khách hàng.
   - Khách hàng: Đặt lịch, xem dịch vụ.

## Công nghệ sử dụng
- **Back-end:**
  - Spring Boot
  - JPA/Hibernate
  - MariaDB

- **Front-end:**
  - ReactJS
  - Tailwind CSS

- **Bảo mật:**
  - Spring Security
  - JSON Web Tokens (JWT)

## Cài đặt và chạy dự án

1. Clone dự án:
   ```bash
   git clone <repo-url>
   cd spa-massage-management
   ```

2. Cài đặt database:
   - Tạo cơ sở dữ liệu MariaDB.
   - Chềnh file `application.properties` trong Spring Boot:
     ```properties
     spring.datasource.url=jdbc:mariadb://localhost:3306/spa_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. Chạy server Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Chạy front-end:
   - Di chuyển đến thư mục frontend:
     ```bash
     cd frontend
     npm install
     npm start
     ```

## API Đã hỗ trợ

- **Xác thực:**
  - `POST /api/auth/login`: Đăng nhập.
  - `POST /api/auth/register`: Đăng ký.

- **Dịch vụ:**
  - `GET /api/services`: Lễ danh sách dịch vụ.
  - `POST /api/services`: Thêm mới dịch vụ.

- **Người dùng:**
  - `GET /api/users`: Lễ danh sách người dùng.

## Đóng góp
Mọi đóng góp, báo lỗi hoặc gửi pull request đều được hoan nghênh!

