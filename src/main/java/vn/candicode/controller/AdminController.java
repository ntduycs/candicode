package vn.candicode.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewAdminRequest;
import vn.candicode.payload.request.UpdateAdminRoleRequest;
import vn.candicode.payload.request.UserPaginatedRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.UserSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.AdminService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
public class AdminController extends Controller {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    protected String getPath() {
        return "admins";
    }

    @PostMapping(path = "admins")
    public ResponseEntity<?> createAdminAccount(@RequestBody @Valid NewAdminRequest payload, @CurrentUser UserPrincipal me) {
        try {
            Long userId = adminService.createAccount(payload, me);

            return ResponseEntity.created(getResourcePath(userId)).body(ResponseFactory.build(
                Map.of("message", "Created new admin account successfully"), HttpStatus.CREATED
            ));
        } catch (IOException e) {
            log.error("Error when creating admin account. Message - {}", e.getLocalizedMessage());

            return ResponseEntity.status(500).body(ResponseFactory.build(
                Map.of("message", "Unexpected error when creating user account"), HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping(path = "admins/{id}/roles")
    public ResponseEntity<?> updateAdminRoles(@PathVariable("id") Long adminId, @RequestBody @Valid UpdateAdminRoleRequest payload, @CurrentUser UserPrincipal me) {
        adminService.updateRole(adminId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated role(s) successfully"
        )));
    }

    @GetMapping(path = "admins/students")
    public ResponseEntity<?> getStudentList(@ModelAttribute UserPaginatedRequest payload, @CurrentUser UserPrincipal admin) {
        PaginatedResponse<UserSummary> items = adminService.getStudentList(payload, admin);

        return ResponseEntity.ok(ResponseFactory.build(items));
    }
}
