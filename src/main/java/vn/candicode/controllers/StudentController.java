package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.StudentRequest;
import vn.candicode.services.v1.StudentService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Log4j2
public class StudentController extends GenericController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudentAccount(@RequestBody @Valid StudentRequest payload) {
        Long studentId = studentService.createStudentAccount(payload);

        return ResponseEntity.created(getResourcePath(studentId)).body(GenericResponse.from(
            Map.of("message", "Registered successfully"), HttpStatus.CREATED
        ));
    }

    @Override
    protected String getResourceBasePath() {
        return "students";
    }
}
