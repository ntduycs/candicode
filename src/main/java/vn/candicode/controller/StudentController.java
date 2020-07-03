package vn.candicode.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.payload.request.UpdateUserProfileRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.StudentService;
import vn.candicode.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
public class StudentController extends Controller {
    private final StudentService studentService;
    private final UserService userService;

    public StudentController(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    @Override
    protected String getPath() {
        return "students";
    }

    @PostMapping(path = "students", produces = {"application/json"})
    public ResponseEntity<?> register(@RequestBody @Valid NewStudentRequest payload) {
        try {
            Long userId = studentService.createAccount(payload);

            return ResponseEntity.created(getResourcePath(userId)).body(ResponseFactory.build(
                Map.of("message", "Registered successfully"), HttpStatus.CREATED
            ));
        } catch (IOException e) {
            log.error("Error when creating student account. Message - {}", e.getLocalizedMessage());

            return ResponseEntity.status(500).body(ResponseFactory.build(
                Map.of("message", "Unexpected error when creating user account"), HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping(path = "students/{id}/roles")
    public ResponseEntity<?> updateStudentRole(@PathVariable("id") Long studentId) {
        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "This feature is coming soon. Please integrate this with Momo implementation"
        )));
    }

    @PostMapping(path = "profiles")
    public ResponseEntity<?> updateStudentProfile(@ModelAttribute @Valid UpdateUserProfileRequest payload, @CurrentUser UserPrincipal me) {
        userService.updateProfile(me.getUserId(), payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated profile successfully"
        )));
    }

}
