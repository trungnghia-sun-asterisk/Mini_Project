# Employee Management System

Employee Management System là ứng dụng Spring Boot quản lý nhân viên và phòng ban, có REST API, giao diện Thymeleaf, JWT authentication, phân quyền USER/ADMIN, validation, reporting, Actuator, cache và scheduler.

## Tính năng

- Employee CRUD, tìm theo tên và phòng ban.
- Department CRUD; không cho xóa phòng ban còn nhân viên liên kết.
- Đăng ký tài khoản, đăng nhập và JWT Bearer token.
- USER chỉ đọc; ADMIN được tạo, sửa và xóa dữ liệu.
- Thymeleaf routes `/employees/list`, `/employees/add`, `/employees/{id}/edit` và `/employees/statistics`.
- Validation và error response JSON thống nhất.
- Báo cáo tổng số nhân viên và số lượng theo phòng ban.
- Actuator health, metrics và caches.
- Cache tổng số nhân viên với Caffeine, TTL 60 giây; cache bị evict sau Employee create/update/delete.
- Scheduled job ghi `System running` mỗi 30 giây.

## Tech stack và kiến trúc

- Java 21, Maven, Spring Boot 3.4.5.
- Spring Web, Spring Data JPA/Hibernate, Bean Validation, Spring Security 6, Thymeleaf.
- MySQL cho profile production; H2 file cho dev và H2 in-memory cho test.
- JJWT 0.12.6, Caffeine, Actuator, JUnit 5, Mockito và MockMvc.

Package chính: `com.example.employeemanagement`.

```text
config/       infrastructure, profiles and security configuration
controller/   api/ REST controllers and web/ Thymeleaf controllers
dto/          request and response records/forms
entity/       Employee, Department, AppUser and Role
exception/    domain exceptions, error DTO and global handler
repository/   Spring Data JPA repositories and projections
security/     UserDetailsService, JWT service and filter
service/      business logic and reporting
scheduler/    30-second health log job
```

Controller chỉ nhận/validate request và gọi service. Service quản lý transaction/business rule; repository chỉ truy cập dữ liệu. Entity không được expose trực tiếp qua API, tránh circular JSON bằng DTO.

## Database và profiles

Profile mặc định là `dev` (`SPRING_PROFILES_ACTIVE` có thể thay đổi). Dev dùng H2 file tại `./data/employee-management`; thư mục này nằm trong `.gitignore`, schema được Hibernate tạo/cập nhật tự động.

Production dùng MySQL và yêu cầu các biến môi trường sau:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://localhost:3306/employee_management?useSSL=false&serverTimezone=UTC
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
JWT_SECRET=<base64-encoded-secret-with-at-least-256-bits>
JWT_EXPIRATION_MS=3600000
```

Tạo database MySQL trước khi chạy production:

```sql
CREATE DATABASE employee_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

`JWT_SECRET` không có giá trị mặc định trong production. Dev/test không cần secret cố định; ứng dụng sinh key tạm thời trong process để tránh hardcode secret. Vì vậy token dev sẽ mất hiệu lực khi restart.

Đăng ký mới luôn có role `USER`. Để cấp quyền admin, operator có thể cập nhật role trong database sau khi đăng ký:

```sql
UPDATE app_users SET role = 'ADMIN' WHERE username = '<registered-username>';
```

Không commit password, JWT secret, API key hoặc file `.env` vào repository.

## Chạy project

Yêu cầu Java 21 và Maven 3.9+:

```powershell
mvn spring-boot:run
```

Hoặc build JAR:

```powershell
mvn clean package
java -jar target/employee-management-0.0.1-SNAPSHOT.jar
```

Chạy profile cụ thể:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
mvn spring-boot:run
```

Kiểm tra nhanh:

```text
GET http://localhost:8080/hello
GET http://localhost:8080/actuator/health
GET http://localhost:8080/login
```

## REST API

| Method | Endpoint | Quyền |
|---|---|---|
| GET | `/api/employees`, `/api/employees/{id}` | USER/ADMIN |
| GET | `/api/employees/search?name=Ada&departmentId=1` | USER/ADMIN |
| POST | `/api/employees` | ADMIN |
| PUT | `/api/employees/{id}` | ADMIN |
| DELETE | `/api/employees/{id}` | ADMIN |
| GET | `/api/departments`, `/api/departments/{id}` | USER/ADMIN |
| POST/PUT/DELETE | `/api/departments[/{id}]` | ADMIN |
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/reports/employees/total` | USER/ADMIN |
| GET | `/api/reports/employees/by-department` | USER/ADMIN |

Tạo department:

```http
POST /api/departments
Content-Type: application/json
Authorization: Bearer <admin-token>

{
  "name": "Engineering",
  "description": "Product engineering"
}
```

Tạo employee:

```http
POST /api/employees
Content-Type: application/json
Authorization: Bearer <admin-token>

{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "departmentId": 1
}
```

Đăng nhập:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "<username>",
  "password": "<password>"
}
```

Response chứa `tokenType`, `accessToken`, `username` và `role`. Gửi token ở header `Authorization: Bearer <accessToken>`.

Lỗi API có dạng:

```json
{
  "timestamp": "2026-08-04T00:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 10 not found",
  "path": "/api/employees/10",
  "errors": {}
}
```

## Thymeleaf UI

- `/login`, `/register`: form login/registration.
- `/employees/list`: danh sách, search theo name/department, navigation.
- `/employees/add`: form thêm employee và hiển thị validation.
- `/employees/{id}/edit`: form sửa employee.
- `/employees/{id}/delete`: POST delete từ UI.
- `/employees/statistics`: tổng số và bảng số lượng theo department.

Nút Add/Edit/Delete chỉ render cho ADMIN bằng Thymeleaf Spring Security dialect. Các form UI dùng CSRF; REST API stateless `/api/**` dùng JWT và tắt CSRF cho API.

## Actuator, cache và scheduling

Các endpoint được expose: `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/caches`. Health public chỉ trả trạng thái; thông tin chi tiết được giới hạn theo authorization.

`ReportingService.totalEmployees()` cache trong cache `employeeTotal` bằng Caffeine với `expireAfterWrite=60s`. Employee service evict toàn bộ cache sau create/update/delete. `SystemStatusScheduler` chạy fixed rate 30 giây và log chính xác `System running`.

## Test và build

```powershell
mvn clean test
mvn clean package
```

Test suite bao gồm context load, repository query, service unit tests, validation/controller tests, JWT, register/login, 401/403, USER/ADMIN, Thymeleaf rendering, reporting API, Actuator và cache eviction. Test dùng H2 in-memory, không cần MySQL.

## GitHub/demo notes

Remote hiện tại là `origin` của repository `trungnghia-sun-asterisk/Mini_Project`, branch phát triển là `main`. Chỉ push sau khi test/build pass, secret scan sạch và remote/branch được kiểm tra.
