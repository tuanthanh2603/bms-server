package com.bms.bms_server.controller;

import com.bms.bms_server.dto.Employee.request.CreateEmployeeDTO;
import com.bms.bms_server.dto.Employee.request.EditEmployeeDTO;
import com.bms.bms_server.dto.Employee.response.AssistantResponseDTO;
import com.bms.bms_server.dto.Employee.response.DriverResponseDTO;
import com.bms.bms_server.dto.Employee.response.EmployeeDTO;
import com.bms.bms_server.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    // UC_EM_01: Thêm nhân viên mới
    @PostMapping("/create")
    public ResponseEntity<Void> createEmployee(@RequestBody CreateEmployeeDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isEmpty() ||
                dto.getFullName() == null || dto.getFullName().isEmpty() ||
                dto.getPassword() == null || dto.getPassword().isEmpty() ||
                dto.getRole() == null || dto.getCompanyId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: Dữ liệu vào không hợp lệ
        }
        if (employeeService.usernameExists(dto.getUsername(), dto.getCompanyId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409: Username đã tồn tại trong công ty này
        }
        try {
            employeeService.createEmployee(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201: Created with no content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400: Dữ liệu vào không hợp lệ
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }


    // UC_EM_02: Xóa nhân viên
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee (@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: ID không hợp lệ
        }
        try {
            if (!employeeService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: ID không tồn tại
            }
            employeeService.deleteEmployeeById(id);
            return ResponseEntity.noContent().build(); // 204: Xóa thành công
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }
    // UC_EM_03: Cập nhật thông tin nhân viên
    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee (@PathVariable Long id, @RequestBody EditEmployeeDTO dto) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: Dữ liệu vào không hợp lệ
        }
        if (dto.getFullName() == null || dto.getFullName().isEmpty() ||
                dto.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: Dữ liệu vào không hợp lệ
        }
        if (!employeeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: User không tồn tại
        }
        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, dto);
            return ResponseEntity.ok(updatedEmployee); // 200: Cap nhat thanh cong
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400: Du lieu vao khong hop le
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Loi he thong
        }
    }

    // UC_EM_04: Khóa tài khoản nhân viên
    @PostMapping("/lock/{id}")
    public ResponseEntity<Void> lockAccountEmployee (@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: ID không hợp lệ
        }
        try {
            if (!employeeService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: ID không tồn tại
            }
            employeeService.lockAccountEmployee(id);
            return ResponseEntity.noContent().build(); // 204: Khóa thành công
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404: ID không tồn tại
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }

    // UC_EM_05: Đặt lại mật khẩu mặc định “12345678”
    @PostMapping("/change-pass/{id}")
    public ResponseEntity<Void> changePassAccountEmployee (@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: ID không hợp lệ
        }
        try {
            if (!employeeService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: ID không tồn tại
            }
            employeeService.changePassAccountEmployee(id);
            return ResponseEntity.noContent().build(); // 204: Thay đổi mật khẩu thành công
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404: ID không tồn tại
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }

    // UC_EM_06: Tìm kiếm nhân viên theo “tên”
    @GetMapping("/filter-by-name")
    public ResponseEntity<List<EmployeeDTO>> searchEmployeesByName(
            @RequestParam("fullName") String fullName,
            @RequestParam("companyId") Long companyId) {

        try {
            List<EmployeeDTO> employees = employeeService.searchEmployeesByName(fullName, companyId);
            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: Không tìm thấy nhân viên
            }
            return ResponseEntity.ok(employees); // 200: Trả về danh sách nhân viên
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }
    // UC_EM_07: Lọc nhân viên theo “vai trò”
    @GetMapping("/filter-by-role")
    public ResponseEntity<List<EmployeeDTO>> searchEmployeesByRole(
            @RequestParam("role") Integer role,
            @RequestParam("companyId") Long companyId) {

        try {
            List<EmployeeDTO> employees = employeeService.searchEmployeesByRole(role, companyId);
            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: Không tìm thấy nhân viên
            }
            return ResponseEntity.ok(employees); // 200: Trả về danh sách nhân viên
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }
    // UC_EM_08: Lấy danh sách nhân viên dựa vào ID của công ty
    @GetMapping("/list-employee/{companyId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCompanyId(@PathVariable Long companyId) {
        if (companyId == null || companyId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400: ID công ty không hợp lệ
        }
        try {
            List<EmployeeDTO> employees = employeeService.getEmployeesByCompanyId(companyId);
            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404: Không có nhân viên nào thuộc công ty
            }
            return ResponseEntity.ok(employees); // 200: Trả về danh sách nhân viên
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Lỗi hệ thống
        }
    }

    @GetMapping("/list-driver/{companyId}")
    public ResponseEntity<List<DriverResponseDTO>> getDriverByCompanyId(@PathVariable Long companyId) {
        try {
            List<DriverResponseDTO> response = employeeService.getDriverByCompanyId(companyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Loi he thong
        }
    }
    @GetMapping("/list-assistant/{companyId}")
    public ResponseEntity<List<AssistantResponseDTO>> getAssistantByCompanyId(@PathVariable Long companyId) {
        try {
            List<AssistantResponseDTO> response = employeeService.getAssistantByCompanyId(companyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500: Loi he thong
        }
    }















}
