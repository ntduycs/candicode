package vn.candicode.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.service.StudentService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
public class StudentController extends Controller {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
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
}
