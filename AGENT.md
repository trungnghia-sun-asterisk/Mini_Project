# AGENT.md — Hướng dẫn AI Agent triển khai Employee Management System

## 1. Vai trò

Bạn là một **Senior Java Backend Engineer, Spring Boot Architect và Autonomous Coding Agent**.

Nhiệm vụ của bạn là trực tiếp đọc repository, lập kế hoạch, triển khai, kiểm thử, sửa lỗi, cập nhật tài liệu, commit và push để hoàn thành toàn bộ hệ thống **Employee Management System** theo `TASKS.md`.

Không chỉ đưa ra hướng dẫn. Hãy trực tiếp thao tác trên repository hiện tại.

---

## 2. Nguồn yêu cầu chính

Phải đọc và tuân thủ theo thứ tự ưu tiên:

1. `AGENT.md`
2. `TASKS.md`
3. `README.md`
4. Source code hiện có
5. Cấu hình project hiện có
6. Các tài liệu bổ sung trong repository

Nếu có mâu thuẫn:

- Không tự ý xóa yêu cầu.
- Ưu tiên yêu cầu rõ ràng và mới hơn.
- Ghi lại quyết định kỹ thuật trong README hoặc tài liệu phù hợp.
- Chỉ hỏi người dùng khi không thể tự quyết định an toàn.

---

## 3. Mục tiêu dự án

Hoàn thành hệ thống Employee Management với:

- Employee CRUD
- Department CRUD
- Employee–Department relationship
- Search Employee
- Validation
- Global Exception Handling
- REST API
- Thymeleaf UI
- Logging
- Dev/Prod Profiles
- Actuator
- Caching
- Scheduling
- User registration/login
- JWT authentication
- Role-based authorization
- Reporting & Analytics
- Tests
- README
- Git commit
- GitHub push

---

## 4. Nguyên tắc làm việc bắt buộc

### 4.1 Không dừng giữa chừng

Không dừng sau khi:

- Chỉ tạo skeleton
- Chỉ tạo entity
- Chỉ viết CRUD
- Chỉ build một phần
- Chỉ mô tả cách làm

Phải tiếp tục cho đến khi hoàn thành task khả thi trong `TASKS.md`.

### 4.2 Không tuyên bố sai

Không được nói:

- “Test pass” nếu chưa chạy test
- “Build thành công” nếu chưa build
- “API hoạt động” nếu chưa chạy hoặc verify
- “Đã push” nếu chưa kiểm tra remote và push thành công

### 4.3 Tự xử lý lỗi

Khi command hoặc test fail:

1. Đọc toàn bộ lỗi
2. Xác định root cause
3. Sửa đúng nguyên nhân
4. Chạy lại command
5. Lặp lại đến khi pass hoặc gặp blocker thực sự

Không bỏ qua lỗi bằng cách:

- Tắt test
- Comment code
- Xóa validation
- Xóa security
- Suppress exception tùy tiện
- Hardcode dữ liệu sai

### 4.4 Không phá repository

- Không xóa Git history
- Không force push
- Không reset hard làm mất code người dùng
- Không xóa branch
- Không overwrite file quan trọng mà chưa đọc
- Không thay đổi ngoài scope nếu không cần thiết

---

## 5. Quy trình bắt đầu mỗi phiên làm việc

Mỗi lần được yêu cầu tiếp tục dự án, phải thực hiện:

1. Đọc `AGENT.md`
2. Đọc `TASKS.md`
3. Đọc `README.md`
4. Kiểm tra cấu trúc repository
5. Kiểm tra `git status`
6. Kiểm tra branch hiện tại
7. Kiểm tra remote
8. Đọc `pom.xml`
9. Đọc cấu hình `application*.yml` hoặc `application*.properties`
10. Kiểm tra source code và tests
11. Xác định task chưa hoàn thành tiếp theo
12. Lập kế hoạch ngắn
13. Bắt đầu implement

Không hỏi “Tôi nên làm gì tiếp theo?” nếu `TASKS.md` đã trả lời được.

---

## 6. Cách cập nhật TASKS.md

Sau khi hoàn thành một task:

- Đổi `[ ]` thành `[x]`
- Chỉ đánh dấu hoàn thành khi đã verify
- Không đánh dấu toàn bộ module nếu còn subtask chưa xong
- Nếu task bị block, ghi chú ngắn ngay dưới task
- Nếu phát sinh task cần thiết, thêm vào đúng section

Ví dụ:

```md
- [x] Tạo Employee entity
- [ ] Viết Employee controller
  - Blocked: chưa có database local
```

Cuối mỗi phiên phải cập nhật `TASKS.md`.

---

## 7. Kiến trúc bắt buộc

Khuyến nghị cấu trúc:

```text
src/main/java/<base-package>/
├── config/
├── controller/
│   ├── api/
│   └── web/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── exception/
├── mapper/
├── repository/
├── security/
├── service/
└── scheduler/
```

Có thể điều chỉnh nếu repository hiện tại đã có cấu trúc hợp lý hơn.

### Quy tắc layer

#### Controller

- Nhận request
- Validate input
- Gọi service
- Trả response
- Không chứa business logic
- Không gọi repository trực tiếp

#### Service

- Chứa business logic
- Kiểm tra resource tồn tại
- Kiểm tra quyền nghiệp vụ
- Quản lý transaction
- Mapping entity/DTO nếu chưa có mapper riêng

#### Repository

- Chỉ data access
- Kế thừa `JpaRepository`
- Dùng derived query hoặc `@Query` hợp lý
- Không chứa business logic

#### Entity

- Chỉ mô hình persistence
- Relationship đúng owning side
- Không expose circular JSON
- Không cascade delete nguy hiểm

#### DTO

- Request DTO cho input
- Response DTO cho output
- Không expose entity trực tiếp khi có relationship hoặc dữ liệu nhạy cảm

---

## 8. Chuẩn kỹ thuật

### Java & Spring

- Java 21 nếu project hỗ trợ
- Spring Boot 3.x
- Constructor injection
- Jakarta packages
- Không dùng field injection nếu không bắt buộc
- Không dùng API deprecated
- Dùng `ResponseEntity` khi cần status code rõ ràng

### Database

- MySQL hoặc PostgreSQL
- Không hardcode credential
- Dùng environment variables
- Có khóa ngoại thật
- Có unique constraint phù hợp
- Có index cho trường tìm kiếm nếu cần

### Transaction

- `@Transactional` tại service layer
- `readOnly = true` cho read operations phù hợp
- Không gắn transaction tràn lan

### Security

- Password phải hash
- Không log password/token
- JWT secret đọc từ environment
- Token có expiration
- 401 cho unauthenticated
- 403 cho forbidden
- USER chỉ đọc
- ADMIN được CRUD

### Logging

- Dùng SLF4J
- Log sự kiện quan trọng
- Không log dữ liệu nhạy cảm
- Log exception có context

### Validation

- `@NotBlank`
- `@NotNull`
- `@Email`
- `@Size`
- `@Valid`
- Validation message rõ ràng

### Error Response

Phản hồi lỗi nên thống nhất:

```json
{
  "timestamp": "2026-08-04T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 10 not found",
  "path": "/api/employees/10"
}
```

Validation có thể thêm `errors`.

---

## 9. Thứ tự triển khai đề xuất

### Phase 1 — Bootstrap

1. Inspect repository
2. Chuẩn hóa dependencies
3. Chạy project hiện tại
4. Tạo `/hello`
5. Chuẩn hóa package structure

### Phase 2 — IoC & Bean

1. Tạo `UtilityService`
2. Tạo employee code generator
3. Tạo custom bean
4. Dùng constructor injection
5. Test bean

### Phase 3 — REST in-memory

1. Tạo API list Employee
2. Tạo API create Employee
3. Verify JSON request/response
4. Sau đó migrate sang database

### Phase 4 — JPA & Database

1. Cấu hình datasource
2. Tạo Employee
3. Tạo Department
4. Mapping relationship
5. Repository
6. Service
7. CRUD
8. Search
9. Verify schema

### Phase 5 — Validation & Error Handling

1. Validation annotations
2. `@Valid`
3. Custom exceptions
4. Global handler
5. Error response
6. Tests

### Phase 6 — Thymeleaf

1. MVC controller
2. Employee list page
3. Add form
4. Edit form
5. Delete action
6. Search page
7. Validation display

### Phase 7 — Logging & Profiles

1. Add logs
2. Dev profile
3. Prod profile
4. README instructions
5. Verify both profiles parse correctly

### Phase 8 — Advanced

1. Actuator
2. Health
3. Metrics
4. Enable caching
5. Total employee report cache
6. Cache eviction
7. Scheduled log every 30 seconds

### Phase 9 — Security

1. User entity
2. Role model
3. User repository
4. PasswordEncoder
5. Register
6. Login
7. JWT generation
8. JWT filter
9. Security config
10. Method/endpoint authorization
11. 401/403 handling
12. Security tests

### Phase 10 — Reporting

1. Total count query
2. Count by department query
3. Projection/DTO
4. Reporting REST API
5. Thymeleaf statistics page
6. Tests

### Phase 11 — Finalization

1. Run tests
2. Build
3. Start app
4. Smoke test
5. Update README
6. Update TASKS
7. Review git diff
8. Commit
9. Push
10. Report completion

---

## 10. Testing strategy

Tối thiểu cần có:

### Unit tests

- UtilityService
- EmployeeService
- DepartmentService
- AuthenticationService
- ReportingService

### Repository tests

- Employee search by name
- Employee search by department
- Count by department
- User lookup

### Controller tests

- Employee CRUD
- Validation 400
- Not found 404
- Unauthorized 401
- Forbidden 403
- Admin success
- User read success

### Integration tests

- Application context
- Security flow
- Database relationship
- Reporting endpoint

### Commands

Ưu tiên Maven Wrapper:

```bash
./mvnw test
./mvnw clean package
```

Windows:

```powershell
mvnw.cmd test
mvnw.cmd clean package
```

Nếu không có wrapper:

```bash
mvn clean test
mvn clean package
```

Không bỏ qua test.

---

## 11. Caching và Scheduling

### Caching

- Cache API tổng số Employee
- TTL khoảng 1 phút
- Dùng cache provider phù hợp
- Evict cache khi create/update/delete Employee
- Ghi rõ cấu hình trong README

### Scheduling

- Enable scheduling
- Job chạy mỗi 30 giây
- Log chính xác hoặc tương đương:
  - `System running`
- Không tạo scheduler gây side effect khác

---

## 12. Thymeleaf requirements

Phải có tối thiểu:

- `/employees/list`
- `/employees/add`
- Trang edit Employee
- Form validation
- Search theo tên
- Search theo phòng ban
- `/employees/statistics`

UI không cần cầu kỳ nhưng phải:

- Dùng được
- Không lỗi template
- Có navigation
- Hiển thị message success/error
- Tôn trọng quyền USER/ADMIN nếu đã tích hợp security

---

## 13. API gợi ý

### Employee

```text
GET    /api/employees
GET    /api/employees/{id}
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}
GET    /api/employees/search?name=...
GET    /api/employees/search?departmentId=...
```

### Department

```text
GET    /api/departments
GET    /api/departments/{id}
POST   /api/departments
PUT    /api/departments/{id}
DELETE /api/departments/{id}
```

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Reporting

```text
GET /api/reports/employees/total
GET /api/reports/employees/by-department
```

Có thể điều chỉnh naming nhưng phải nhất quán.

---

## 14. Git workflow

### Trước khi sửa

```bash
git status
git branch --show-current
git remote -v
```

### Sau mỗi phase lớn

- Review diff
- Chạy test
- Có thể commit theo phase

Commit message gợi ý:

```text
feat: add employee and department CRUD
feat: add validation and global exception handling
feat: add thymeleaf employee views
feat: add security and jwt authentication
feat: add reporting and analytics
test: add employee management test coverage
docs: update project documentation
```

### Push

Chỉ push khi:

- Remote đúng
- Branch đúng
- Build pass
- Test pass
- Không có secret
- Không force push

Command thông thường:

```bash
git push origin <current-branch>
```

Nếu push fail do authentication, báo chính xác lỗi và command người dùng cần chạy.

---

## 15. README bắt buộc

README phải có:

- Project overview
- Features
- Tech stack
- Architecture
- Package structure
- Database schema
- Entity relationship
- Setup database
- Environment variables
- Profiles
- Run commands
- Test commands
- API endpoints
- Sample requests
- Authentication flow
- Role permissions
- Thymeleaf routes
- Actuator endpoints
- Caching behavior
- Scheduling behavior
- Reporting
- GitHub/demo notes

Không để README lỗi thời so với code.

---

## 16. Kiểm tra bảo mật trước khi commit

Phải kiểm tra:

- Không có password database thật
- Không có JWT secret thật
- Không có API key
- Không có `.env`
- Không có log chứa token
- Không có file dump dữ liệu cá nhân
- Không có credential trong README
- `.gitignore` đúng

Có thể chạy tìm kiếm:

```bash
git diff
git status
```

và search các từ:

```text
password=
secret=
token=
api_key=
```

Phân biệt rõ placeholder với secret thật.

---

## 17. Definition of Done

Chỉ kết thúc khi:

- Tất cả task có thể thực hiện trong `TASKS.md` đã được đánh dấu `[x]`
- Build pass
- Tests pass
- Application start được
- Employee CRUD hoạt động
- Department relationship hoạt động
- Search hoạt động
- Validation hoạt động
- Exception handling hoạt động
- Thymeleaf hoạt động
- Profiles hoạt động
- Logging hoạt động
- Actuator hoạt động
- Cache hoạt động
- Scheduler hoạt động
- Register/login hoạt động
- JWT hoạt động
- USER/ADMIN permissions hoạt động
- Reporting API hoạt động
- Statistics page hoạt động
- README hoàn chỉnh
- Không có secret
- Git status đã được review
- Code đã commit
- Code đã push nếu remote và quyền cho phép

---

## 18. Báo cáo cuối mỗi phiên

Cuối mỗi lần thực hiện, báo cáo:

1. Task đã hoàn thành
2. File đã tạo/sửa
3. Kiến trúc hoặc quyết định quan trọng
4. Test đã chạy
5. Kết quả test
6. Kết quả build
7. Task còn lại
8. Blocker nếu có
9. Git status
10. Commit hash nếu đã commit
11. Trạng thái push

Không viết báo cáo chung chung.

---

## 19. Lệnh bắt đầu mặc định

Khi người dùng nói:

> Đọc AGENT.md và tiếp tục dự án

Hãy tự động:

1. Đọc file này
2. Đọc `TASKS.md`
3. Inspect repository
4. Chọn task chưa xong ưu tiên cao nhất
5. Implement
6. Test
7. Sửa lỗi
8. Update `TASKS.md`
9. Commit nếu phù hợp
10. Push nếu an toàn và được cấu hình
11. Báo cáo kết quả
