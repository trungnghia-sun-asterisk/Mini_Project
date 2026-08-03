# TASKS.md — Employee Management System

> Nguồn yêu cầu: **Lộ trình Học Spring Boot – Mini Project: Employee Management System**

## 0. Mục tiêu cuối cùng

Xây dựng hoàn chỉnh hệ thống **Employee Management System** bằng:

- Spring Boot
- Spring Data JPA
- REST API
- Thymeleaf
- MySQL hoặc PostgreSQL
- Spring Security
- JWT cơ bản
- Validation
- Exception Handling
- Logging
- Profiles
- Actuator
- Scheduling
- Caching
- Reporting & Analytics

## 1. Chức năng tổng thể

- [x] Quản lý nhân viên:
  - [ ] Thêm nhân viên
  - [ ] Sửa nhân viên
  - [ ] Xóa nhân viên
  - [ ] Xem chi tiết nhân viên
  - [ ] Xem danh sách nhân viên
  - [ ] Tìm kiếm nhân viên theo tên
  - [ ] Tìm kiếm nhân viên theo phòng ban
- [x] Quản lý phòng ban
- [x] Đăng ký tài khoản
- [x] Đăng nhập
- [x] Phân quyền `ADMIN` / `USER`
- [x] Thống kê dữ liệu nhân viên:
  - [ ] Tổng số nhân viên
  - [ ] Số lượng nhân viên theo phòng ban
  - [ ] Hiển thị thống kê bằng REST API
  - [ ] Hiển thị thống kê bằng Thymeleaf

---

# MODULE 1 — Getting Started with Spring Boot

## Nội dung cần hoàn thành

- [x] Khởi tạo project Spring Boot bằng Spring Initializr
- [x] Đặt tên project: `employee-management`
- [x] Cấu hình Java và Maven
- [x] Thêm các dependency cơ bản:
  - [ ] Spring Web
  - [ ] Spring Data JPA
  - [ ] Thymeleaf
  - [ ] Validation
  - [ ] Database Driver
  - [ ] Lombok nếu sử dụng
- [x] Tổ chức cấu trúc package rõ ràng
- [x] Tạo class main Spring Boot
- [x] Chạy project thành công

## Lab 1

- [x] Tạo endpoint `GET /hello`
- [x] Endpoint trả về thông báo xác nhận hệ thống chạy thành công
- [x] Kiểm tra bằng browser, Postman hoặc curl
- [x] Ghi hướng dẫn chạy trong README

---

# MODULE 2 — Custom Bean & IoC

## Nội dung cần hoàn thành

- [x] Sử dụng `@Component`
- [x] Sử dụng `@Service`
- [x] Sử dụng `@Repository`
- [x] Tạo ít nhất một bean bằng `@Bean`
- [x] Tạo class `@Configuration`
- [x] Sử dụng constructor injection
- [x] Tránh field injection nếu không cần thiết

## Lab 2

- [x] Tạo `UtilityService`
- [x] Thêm chức năng format chuỗi
- [x] Thêm chức năng tạo mã nhân viên tự động
- [x] Đánh dấu service bằng `@Service`
- [x] Tạo custom bean bằng `@Bean`
- [x] Chọn một trong:
  - [ ] `PasswordEncoder`
  - [ ] `ModelMapper`
- [x] Inject bean vào Controller hoặc Service
- [x] Viết test hoặc endpoint chứng minh bean hoạt động

---

# MODULE 3 — REST API cơ bản

## Nội dung cần hoàn thành

- [x] Sử dụng `@RestController`
- [x] Sử dụng `@RequestMapping`
- [x] Sử dụng `@GetMapping`
- [x] Sử dụng `@PostMapping`
- [x] Sử dụng `@PathVariable`
- [x] Sử dụng `@RequestParam`
- [x] Sử dụng `@RequestBody`
- [x] Sử dụng `ResponseEntity`

## Lab 3

- [x] Tạo model Employee tạm thời
- [x] Tạo danh sách Employee in-memory
- [x] Tạo API lấy danh sách nhân viên
- [x] Tạo API thêm nhân viên mới
- [x] Kiểm tra request/response JSON
- [x] Chuẩn bị cấu trúc để chuyển sang database ở Module 4

> Ghi chú: phần in-memory của Lab 3 được hiện thực trực tiếp bằng JPA ở Module 4 để dữ liệu được lưu bền vững; các hành vi CRUD tương ứng đã được kiểm thử.

---

# MODULE 4 — Spring Boot + Database

## Database

- [x] Chọn MySQL hoặc PostgreSQL
- [x] Tạo database
- [x] Cấu hình datasource
- [x] Cấu hình Hibernate/JPA
- [x] Không commit mật khẩu thật
- [x] Sử dụng environment variables hoặc file local riêng

## Entity

### Employee

- [x] Tạo entity `Employee`
- [x] Có các trường:
  - [ ] `id`
  - [ ] `name`
  - [ ] `email`
  - [ ] `department`
- [x] Ánh xạ bảng `employee`
- [x] Cấu hình khóa chính
- [x] Cấu hình tự sinh ID

### Department

- [x] Tạo entity `Department`
- [x] Có các trường cơ bản:
  - [ ] `id`
  - [ ] `name`
  - [ ] `description` nếu cần
- [x] Ánh xạ bảng `department`

## Quan hệ dữ liệu

- [x] Thiết lập quan hệ giữa `Employee` và `Department`
- [x] Một Department có nhiều Employee
- [x] Một Employee thuộc một Department
- [x] Sử dụng:
  - [ ] `@ManyToOne`
  - [ ] `@OneToMany`
  - [ ] `@JoinColumn`
- [x] Tránh vòng lặp JSON
- [x] Không dùng cascade delete nguy hiểm

## Repository

- [x] Tạo `EmployeeRepository`
- [x] Tạo `DepartmentRepository`
- [x] Kế thừa `JpaRepository`

## Service

- [x] Tạo `EmployeeService`
- [x] Tạo `DepartmentService`
- [x] Tách business logic khỏi controller

## CRUD Employee

- [x] Tạo Employee
- [x] Lấy danh sách Employee
- [x] Lấy Employee theo ID
- [x] Cập nhật Employee
- [x] Xóa Employee
- [x] Kiểm tra tồn tại Department khi tạo/cập nhật Employee

## CRUD Department

- [x] Tạo Department
- [x] Lấy danh sách Department
- [x] Lấy Department theo ID
- [x] Cập nhật Department
- [x] Xóa Department
- [x] Không cho xóa Department nếu đang có Employee liên kết

## Tìm kiếm

- [x] Tìm Employee theo tên
- [x] Tìm Employee theo phòng ban
- [x] Sử dụng derived query hoặc `@Query`
- [x] Kiểm tra kết quả tìm kiếm

---

# MODULE 5 — Validation & Exception Handling

## Validation

- [x] Thêm validation cho Employee
- [x] `name` không được rỗng
- [x] `email` đúng định dạng
- [x] `email` không được rỗng
- [x] `departmentId` hợp lệ
- [x] Sử dụng `@Valid`

## Exception Handling

- [x] Tạo `ResourceNotFoundException`
- [x] Xử lý Employee không tồn tại
- [x] Xử lý Department không tồn tại
- [x] Trả HTTP `404 Not Found`
- [x] Tạo `GlobalExceptionHandler`
- [x] Sử dụng `@ControllerAdvice` hoặc `@RestControllerAdvice`
- [x] Xử lý request sai định dạng
- [x] Xử lý validation error
- [x] Trả message lỗi rõ ràng
- [x] Chuẩn hóa cấu trúc error response

---

# MODULE 6 — Spring Boot Web MVC + Thymeleaf

## Cấu hình

- [x] Thêm `spring-boot-starter-thymeleaf`
- [x] Tạo MVC Controller
- [x] Tạo thư mục templates
- [x] Tạo layout cơ bản
- [x] Tạo navigation

## Lab 6

- [x] Tạo trang `/employees/list`
- [x] Hiển thị danh sách Employee từ database
- [x] Tạo trang `/employees/add`
- [x] Tạo form thêm Employee
- [x] Xử lý submit form
- [x] Hiển thị validation error trên form
- [x] Tạo trang sửa Employee
- [x] Tạo chức năng xóa Employee từ giao diện
- [x] Tạo trang tìm kiếm Employee
- [x] Tìm theo tên
- [x] Tìm theo phòng ban
- [x] Hiển thị kết quả tìm kiếm

---

# MODULE 7 — Logging & Profiles

## Logging

- [x] Sử dụng SLF4J
- [x] Sử dụng Logback mặc định
- [x] Log khi thêm Employee
- [x] Log khi sửa Employee
- [x] Log khi xóa Employee
- [x] Log khi có lỗi
- [x] Không log password hoặc dữ liệu nhạy cảm

## Profiles

- [x] Tạo `application-dev.yml`
- [x] Tạo `application-prod.yml`
- [x] Tách cấu hình database dev/prod
- [x] Cấu hình profile mặc định phù hợp
- [x] Ghi hướng dẫn chọn profile trong README
- [x] Không commit secret production

---

# MODULE 8 — Advanced Spring Boot

## Actuator

- [x] Thêm Spring Boot Actuator
- [x] Bật health endpoint
- [x] Bật metrics cần thiết
- [x] Kiểm tra `/actuator/health`
- [x] Kiểm tra các endpoint actuator được phép expose

## Caching

- [x] Bật caching bằng `@EnableCaching`
- [x] Tạo API báo cáo tổng số Employee
- [x] Cache kết quả báo cáo
- [x] TTL cache khoảng 1 phút
- [x] Xóa hoặc cập nhật cache khi dữ liệu Employee thay đổi
- [x] Kiểm tra cache hoạt động

## Scheduling

- [x] Bật scheduling
- [x] Tạo scheduled task chạy mỗi 30 giây
- [x] Log đúng nội dung: `System running`
- [x] Kiểm tra task chạy ổn định

---

# MODULE 9 — Spring Security Basics

## User & Role

- [x] Tạo entity `User`
- [x] Có các trường:
  - [ ] `id`
  - [ ] `username`
  - [ ] `password`
  - [ ] `role`
- [x] Tạo role `ADMIN`
- [x] Tạo role `USER`
- [x] Mã hóa password bằng `PasswordEncoder`

## Authentication

- [x] Tích hợp Spring Security
- [x] Tạo API đăng ký
- [x] Tạo API đăng nhập
- [x] Kiểm tra username trùng
- [x] Không lưu password dạng plain text
- [x] Tạo JWT cơ bản
- [x] Sinh access token khi đăng nhập
- [x] Kiểm tra token cho API được bảo vệ
- [x] Xử lý token không hợp lệ
- [x] Xử lý token hết hạn

## Authorization

- [x] USER chỉ được xem danh sách Employee
- [x] USER được xem chi tiết Employee
- [x] USER không được tạo Employee
- [x] USER không được sửa Employee
- [x] USER không được xóa Employee
- [x] ADMIN được CRUD Employee
- [x] Bảo vệ endpoint bằng cấu hình Security hoặc method security
- [x] Trả HTTP 401 khi chưa xác thực
- [x] Trả HTTP 403 khi không đủ quyền

## Thymeleaf Security

- [x] Tích hợp đăng nhập cho giao diện nếu nằm trong scope triển khai
- [x] Ẩn nút thêm/sửa/xóa với USER
- [x] Hiển thị nút quản trị với ADMIN

---

# MODULE 10 — Reporting & Analytics

## Repository Queries

- [x] Viết query tính tổng số Employee
- [x] Viết query thống kê Employee theo Department
- [x] Sử dụng `@Query` trong Repository
- [x] Thiết kế DTO/projection cho kết quả thống kê

## REST API

- [x] Tạo API tổng số Employee
- [x] Tạo API thống kê Employee theo Department
- [x] Trả dữ liệu JSON rõ ràng
- [x] Bảo vệ quyền truy cập phù hợp

## Thymeleaf

- [x] Tạo trang `/employees/statistics`
- [x] Hiển thị tổng số Employee
- [x] Hiển thị số Employee theo Department
- [x] Trình bày dễ đọc
- [x] Có thể dùng bảng hoặc biểu đồ đơn giản

---

# KIẾN TRÚC VÀ CHẤT LƯỢNG MÃ NGUỒN

- [x] Tổ chức package clean
- [x] Sử dụng DTO request/response
- [x] Không expose entity trực tiếp nếu gây coupling hoặc circular JSON
- [x] Dùng constructor injection
- [x] Controller mỏng
- [x] Business logic nằm ở Service
- [x] Repository chỉ làm data access
- [x] Dùng transaction đúng chỗ
- [x] Dùng `readOnly = true` cho truy vấn khi phù hợp
- [x] Dùng `BigDecimal`, `LocalDate`, `LocalDateTime` đúng ngữ cảnh
- [x] Không để TODO chưa xử lý
- [x] Không để code chết
- [x] Không hardcode secret
- [x] Không commit file build
- [x] Không commit file IDE không cần thiết

---

# TESTING

- [x] Context load test
- [x] Repository test cho Employee
- [x] Repository test cho Department
- [x] Test tìm Employee theo tên
- [x] Test tìm Employee theo Department
- [x] Service test CRUD Employee
- [x] Service test exception
- [x] Controller/API test cho Employee
- [x] Security test USER
- [x] Security test ADMIN
- [x] Test validation
- [x] Test reporting query
- [x] Chạy toàn bộ test thành công

---

# DOCUMENTATION

- [x] Tạo README.md
- [x] Mô tả project
- [x] Liệt kê tính năng
- [x] Liệt kê tech stack
- [x] Mô tả kiến trúc
- [x] Mô tả database schema
- [x] Mô tả quan hệ Employee–Department
- [x] Hướng dẫn cấu hình database
- [x] Hướng dẫn chọn profile
- [x] Hướng dẫn chạy project
- [x] Hướng dẫn chạy test
- [x] Liệt kê API
- [x] Thêm sample request/response
- [x] Mô tả role USER/ADMIN
- [x] Mô tả JWT flow
- [x] Mô tả Thymeleaf routes
- [x] Mô tả Actuator
- [x] Mô tả caching và scheduling

---

# GIT & HOÀN THIỆN

- [x] Tạo `.gitignore`
- [x] Kiểm tra không có secret
- [x] Kiểm tra `git status`
- [x] Chạy format nếu project có formatter
- [x] Chạy `mvn clean test`
- [x] Chạy `mvn clean package`
- [x] Chạy application
- [x] Test API chính
- [x] Kiểm tra trang Thymeleaf
- [x] Kiểm tra security
- [x] Kiểm tra reporting
- [x] Kiểm tra actuator
- [x] Kiểm tra scheduler
- [x] Kiểm tra cache
- [x] Cập nhật trạng thái task trong file này
- [ ] Commit code
- [ ] Push lên GitHub
- [ ] Kiểm tra repository GitHub sau khi push

---

# DEFINITION OF DONE

Project chỉ được coi là hoàn thành khi:

- [x] Toàn bộ 10 module đã được triển khai
- [x] REST API hoạt động
- [x] Thymeleaf UI hoạt động
- [x] Database relationship hoạt động
- [x] CRUD Employee hoàn chỉnh
- [x] CRUD Department hoàn chỉnh
- [x] Validation hoạt động
- [x] Exception handling hoạt động
- [x] Logging hoạt động
- [x] Profiles hoạt động
- [x] Actuator hoạt động
- [x] Cache hoạt động
- [x] Scheduled task hoạt động
- [x] Đăng ký/đăng nhập hoạt động
- [x] JWT hoạt động
- [x] Phân quyền USER/ADMIN hoạt động
- [x] Reporting API hoạt động
- [x] Trang thống kê hoạt động
- [x] Tests pass
- [x] Build pass
- [x] README hoàn chỉnh
- [x] Không có secret
- [ ] Code đã commit
- [ ] Code đã push lên GitHub
